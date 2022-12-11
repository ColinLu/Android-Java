package com.colin.library.android.widgets.edge;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.annotation.Orientation;


public class EdgeLayout extends FrameLayout implements NestedScrollingParent3 {
    ///////////////////////////////////////////////////////////////////////////
    // 对外公开接口
    ///////////////////////////////////////////////////////////////////////////

    public interface StopTargetViewFlingImpl {
        default void stopFling(View view) {
        }
    }

    public static final Interpolator INTERPOLATOR = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };
    public static final int DEFAULT_MIN_SCROLL_DURATION = 300;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PULLING = 1;
    private static final int STATE_SETTLING_TO_TRIGGER_OFFSET = 2;
    private static final int STATE_TRIGGERING = 3;
    private static final int STATE_SETTLING_TO_INIT_OFFSET = 4;
    private static final int STATE_SETTLING_DELIVER = 5;
    private static final int STATE_SETTLING_FLING = 6;

    private final int[] mNestedScrollingV2ConsumedCompat = new int[2];

    private int mDirectionEnabled;
    private View mTargetView;
    private Edge mEdgeLeft = null;
    private Edge mEdgeTop = null;
    private Edge mEdgeRight = null;
    private Edge mEdgeBottom = null;
    private OnEdgeListener mOnEdgeListener;
    private ViewOffsetHelper mTargetViewOffsetHelper;
    private StopTargetViewFlingImpl mStopTargetViewFlingImpl = DefaultStopTargetViewFlingImpl.getInstance();
    private Runnable mStopTargetFlingRunnable = null;
    private float mNestedPreFlingVelocityScaleDown = 10;
    private int mMinScrollDuration = DEFAULT_MIN_SCROLL_DURATION;
    private int mState = STATE_IDLE;

    private final OverScroller mScroller;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;

    public EdgeLayout(@NonNull Context context) {
        this(context, null, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout, defStyleAttr, 0);
        mDirectionEnabled = array.getInt(R.styleable.EdgeLayout_direction, Direction.ALL);
        array.recycle();
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mScroller = new OverScroller(context, INTERPOLATOR);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int count = getChildCount();
        if (count == 0) return;
        boolean isTarget = false;
        int set = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.mTarget) {
                if (isTarget) throw new RuntimeException("target view more than one");
                isTarget = true;
                setTargetView(child);
            } else {
                if ((set & params.mDirection) != 0)
                    throw new RuntimeException("same direction edge view more than one");
                set |= params.mDirection;
                setEdgeView(child, params);
            }
        }
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(@NonNull ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int w = r - l;
        final int h = b - t;
        if (mTargetView != null) {
            mTargetView.layout(0, 0, w, h);
            mTargetViewOffsetHelper.onViewLayout();
        }
        if (mEdgeLeft != null) mEdgeLeft.onLayout(w, h);
        if (mEdgeTop != null) mEdgeTop.onLayout(w, h);
        if (mEdgeRight != null) mEdgeRight.onLayout(w, h);
        if (mEdgeBottom != null) mEdgeBottom.onLayout(w, h);
    }

    @Override
    public void computeScroll() {
        if (!mScroller.computeScrollOffset()) return;
        if (!mScroller.isFinished()) {
            edgeUpdateOffset(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidateOnAnimation();
            return;
        }
        if (mState == STATE_TRIGGERING) return;

        if (mState == STATE_SETTLING_TO_INIT_OFFSET) {
            mState = STATE_IDLE;
            return;
        }
        if (mState == STATE_SETTLING_FLING) {
            checkScroll();
            return;
        }
        if (mState == STATE_SETTLING_TO_TRIGGER_OFFSET) {
            final int finalX = mScroller.getFinalX();
            final int finalY = mScroller.getFinalY();
            checkFinal(getDirectionEdge(Direction.LEFT), finalX, finalY);
            checkFinal(getDirectionEdge(Direction.TOP), finalX, finalY);
            checkFinal(getDirectionEdge(Direction.RIGHT), finalX, finalY);
            checkFinal(getDirectionEdge(Direction.BOTTOM), finalX, finalY);
            edgeUpdateOffset(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {
        return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes, @ViewCompat.NestedScrollType int type) {
        return mTargetView == target && (isStartNestedScrollHorizontal(axes) || isStartNestedScrollVertical(axes));
    }


    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes, @ViewCompat.NestedScrollType int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable();
            mScroller.abortAnimation();
            mState = STATE_PULLING;
        }
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(@NonNull View child) {
        onStopNestedScroll(child, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, @ViewCompat.NestedScrollType int type) {
        if (mState == STATE_PULLING) checkScroll();
        else if (mState == STATE_SETTLING_DELIVER && type != ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable();
            checkScroll();
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH, mNestedScrollingV2ConsumedCompat);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @ViewCompat.NestedScrollType int type) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, mNestedScrollingV2ConsumedCompat);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @ViewCompat.NestedScrollType int type, @NonNull int[] consumed) {
        final int srcDx = dxUnconsumed, srcDy = dyUnconsumed;
        dxUnconsumed = getScroll(getDirectionEdge(Direction.LEFT), Orientation.HORIZONTAL, -1, dxUnconsumed, consumed, type);
        dxUnconsumed = getFling(getDirectionEdge(Direction.RIGHT), Orientation.HORIZONTAL, 1, dxUnconsumed, consumed, type);
        dxUnconsumed = getFling(getDirectionEdge(Direction.LEFT), Orientation.HORIZONTAL, -1, dxUnconsumed, consumed, type);
        dxUnconsumed = getScroll(getDirectionEdge(Direction.RIGHT), Orientation.HORIZONTAL, 1, dxUnconsumed, consumed, type);

        dyUnconsumed = getScroll(getDirectionEdge(Direction.TOP), Orientation.VERTICAL, -1, dyUnconsumed, consumed, type);
        dyUnconsumed = getFling(getDirectionEdge(Direction.BOTTOM), Orientation.VERTICAL, 1, dyUnconsumed, consumed, type);
        dyUnconsumed = getFling(getDirectionEdge(Direction.TOP), Orientation.VERTICAL, -1, dyUnconsumed, consumed, type);
        dyUnconsumed = getScroll(getDirectionEdge(Direction.BOTTOM), Orientation.VERTICAL, 1, dyUnconsumed, consumed, type);

        if (dyUnconsumed == srcDy && dxUnconsumed == srcDx && mState == STATE_SETTLING_DELIVER) {
            stopTargetFling(target, dxUnconsumed, dyUnconsumed, type);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedPreScroll(@NonNull final View target, @Px int dx, @Px int dy, @NonNull int[] consumed, @ViewCompat.NestedScrollType int type) {
        final int srcDx = dx, srcDy = dy;
        dx = getScroll(getDirectionEdge(Direction.LEFT), Orientation.HORIZONTAL, -1, dx, consumed, type);
        dx = getFling(getDirectionEdge(Direction.RIGHT), Orientation.HORIZONTAL, 1, dx, consumed, type);
        dx = getFling(getDirectionEdge(Direction.LEFT), Orientation.HORIZONTAL, -1, dx, consumed, type);
        dx = getScroll(getDirectionEdge(Direction.RIGHT), Orientation.HORIZONTAL, 1, dx, consumed, type);

        dy = getScroll(getDirectionEdge(Direction.TOP), Orientation.VERTICAL, -1, dy, consumed, type);
        dy = getFling(getDirectionEdge(Direction.BOTTOM), Orientation.VERTICAL, 1, dy, consumed, type);
        dy = getFling(getDirectionEdge(Direction.TOP), Orientation.VERTICAL, -1, dy, consumed, type);
        dy = getScroll(getDirectionEdge(Direction.BOTTOM), Orientation.VERTICAL, 1, dy, consumed, type);

        if (srcDx == dx && srcDy == dy && mState == STATE_SETTLING_DELIVER) {
            stopTargetFling(target, dx, dy, type);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        final int offsetX = mTargetViewOffsetHelper.getLeftAndRightOffset();
        final int offsetY = mTargetViewOffsetHelper.getTopAndBottomOffset();
        // if the targetView is RecyclerView and we set OnFlingListener for RecyclerView.
        // then the targetView can not deliver fling consume to NestedScrollParent
        // so we intercept the fling if the target view can not consume the fling.
        if (onNestedPreFlingHorizontally(getDirectionEdge(Direction.LEFT), offsetX, offsetY, velocityX, -1)) {
            return true;
        }
        if (onNestedPreFlingVertically(getDirectionEdge(Direction.TOP), offsetX, offsetY, velocityY, -1)) {
            return true;
        }
        if (onNestedPreFlingHorizontally(getDirectionEdge(Direction.RIGHT), offsetX, offsetY, velocityX, 1)) {
            return true;
        }
        if (onNestedPreFlingVertically(getDirectionEdge(Direction.BOTTOM), offsetX, offsetY, velocityY, 1)) {
            return true;
        }
        mState = STATE_SETTLING_DELIVER;
        return super.onNestedPreFling(target, velocityX, velocityY);
    }


    public void setTargetView(@NonNull View view) {
        if (view.getParent() != this) {
            throw new RuntimeException("Target already exists other parent view.");
        }
        if (view.getParent() == null) {
            addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        mTargetView = view;
        mTargetViewOffsetHelper = new ViewOffsetHelper(view);
    }

    public void setEdgeView(@NonNull View view, @NonNull LayoutParams params) {
        final Edge.Builder builder = new Edge.Builder(view, params.mDirection)
                .setEdgeOver(params.mEdgeOver)
                .setEdgeRate(params.mEdgeRate)
                .setScrollTouchUp(params.mScrollTouchUp)
                .setFlingFromTarget(params.mFlingFromTarget)
                .setScrollFling(params.mScrollFling)
                .setStartOffset(params.mStartOffset)
                .setScrollOffset(params.mScrollOffset)
                .setTargetOffset(params.mTargetOffset)
                .setScrollSpeed(params.mScrollSpeed);
        view.setLayoutParams(params);
        setEdgeView(builder);
    }

    public void setEdgeView(@NonNull Edge.Builder builder) {
        if (builder.getView().getParent() != this) {
            throw new RuntimeException("Action view already exists other parent view.");
        }
        if (builder.getView().getParent() == null) {
            ViewGroup.LayoutParams lp = builder.getView().getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            addView(builder.getView(), lp);
        }
        if (builder.getDirection() == Direction.LEFT) {
            mEdgeLeft = builder.build();
        } else if (builder.getDirection() == Direction.TOP) {
            mEdgeTop = builder.build();
        } else if (builder.getDirection() == Direction.RIGHT) {
            mEdgeRight = builder.build();
        } else if (builder.getDirection() == Direction.BOTTOM) {
            mEdgeBottom = builder.build();
        }
    }

    public void setDirectionEnabled(@Direction int direction) {
        mDirectionEnabled = direction;
    }

    public int getDirectionEnabled() {
        return mDirectionEnabled;
    }

    public boolean isDirectionEnabled(@Direction int direction) {
        return (mDirectionEnabled & direction) == direction;
    }

    @Nullable
    public Edge getDirectionEdge(@Direction int direction) {
        return isDirectionEnabled(direction) ? getEdgeView(direction) : null;
    }

    @Nullable
    private Edge getEdgeView(@Direction int direction) {
        if (direction == Direction.LEFT) return mEdgeLeft;
        if (direction == Direction.TOP) return mEdgeTop;
        if (direction == Direction.RIGHT) return mEdgeRight;
        if (direction == Direction.BOTTOM) return mEdgeBottom;
        return null;
    }


    public void setNestedPreFlingVelocityScaleDown(float nestedPreFlingVelocityScaleDown) {
        this.mNestedPreFlingVelocityScaleDown = nestedPreFlingVelocityScaleDown;
    }


    public void setEdgeListener(@Nullable OnEdgeListener edgeListener) {
        this.mOnEdgeListener = edgeListener;
    }

    public void setStopTargetViewFlingImpl(@Nullable StopTargetViewFlingImpl stopTargetViewFlingImpl) {
        mStopTargetViewFlingImpl = stopTargetViewFlingImpl;
    }

    public void setMinScrollDuration(int minScrollDuration) {
        mMinScrollDuration = minScrollDuration;
    }


    public void edgeStart(@NonNull Edge edge) {
        if (mState != STATE_TRIGGERING && edge.isRunning()) return;
        mState = STATE_TRIGGERING;
        edge.setRunning(true);
        if (mOnEdgeListener != null) mOnEdgeListener.start(edge);
    }

    public void edgeFinish() {
        if (mEdgeLeft != null) edgeFinish(mEdgeLeft, Orientation.HORIZONTAL, true);
        if (mEdgeTop != null) edgeFinish(mEdgeTop, Orientation.VERTICAL, true);
        if (mEdgeRight != null) edgeFinish(mEdgeRight, Orientation.HORIZONTAL, true);
        if (mEdgeBottom != null) edgeFinish(mEdgeBottom, Orientation.VERTICAL, true);
    }

    public void edgeFinish(@NonNull Edge edge, @Orientation int orientation) {
        if (edge.isRunning()) edgeFinish(edge, orientation, true);
    }

    public void edgeFinish(@NonNull Edge edge, @Orientation int orientation, boolean animate) {
        if (!edge.isRunning()) return;
        if (mOnEdgeListener != null) mOnEdgeListener.finish(edge);
        edge.setRunning(false);
        if (mState == STATE_PULLING) return;
        if (!animate) {
            mState = STATE_IDLE;
            mTargetViewOffsetHelper.setOrientationOffset(orientation, 0);
            edge.updateOffset(0);
            return;
        }
        mState = STATE_SETTLING_TO_INIT_OFFSET;
        final int direction = edge.getDirection();
        final int offsetX = mTargetViewOffsetHelper.getLeftAndRightOffset();
        final int offsetY = mTargetViewOffsetHelper.getTopAndBottomOffset();
        if (direction == Direction.LEFT && offsetX > 0) {
            mScroller.startScroll(offsetX, offsetY, -offsetX, 0, scrollDuration(edge, offsetX));
            postInvalidateOnAnimation();
            return;
        }
        if (direction == Direction.TOP && offsetY > 0) {
            mScroller.startScroll(offsetX, offsetY, 0, -offsetY, scrollDuration(edge, offsetY));
            postInvalidateOnAnimation();
            return;
        }
        if (direction == Direction.RIGHT && offsetX < 0) {
            mScroller.startScroll(offsetX, offsetY, -offsetX, 0, scrollDuration(edge, offsetX));
            postInvalidateOnAnimation();
            return;
        }
        if (direction == Direction.BOTTOM && offsetY < 0) {
            mScroller.startScroll(offsetX, offsetY, 0, -offsetY, scrollDuration(edge, offsetY));
            postInvalidateOnAnimation();
            return;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////
    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean mTarget = false;
        public boolean mEdgeOver;
        public boolean mFlingFromTarget = true;
        public boolean mScrollOffset = false;
        public boolean mScrollTouchUp = true;
        @Direction
        public int mDirection;
        public int mStartOffset;
        public int mTargetOffset = ViewGroup.LayoutParams.WRAP_CONTENT;
        public float mEdgeRate = Edge.EDGE_RATE_DEFAULT;
        public float mScrollFling = Constants.SCROLL_FLING_DEFAULT;
        public float mScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;


        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout_Layout);
            mTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_target, mTarget);
            if (!mTarget) {
                mDirection = array.getInteger(R.styleable.EdgeLayout_Layout_direction, Direction.TOP);
                mTargetOffset = array.getLayoutDimension(R.styleable.EdgeLayout_Layout_edgeTargetOffset, mTargetOffset);
                mStartOffset = array.getDimensionPixelSize(R.styleable.EdgeLayout_Layout_edgeStartOffset, mStartOffset);
                mEdgeOver = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeOver, mEdgeOver);
                mEdgeRate = array.getFloat(R.styleable.EdgeLayout_Layout_edgeRate, mEdgeRate);
                mScrollFling = array.getFloat(R.styleable.EdgeLayout_Layout_edgeFlingFraction, mScrollFling);
                mScrollSpeed = array.getFloat(R.styleable.EdgeLayout_Layout_edgeScrollSpeed, mScrollSpeed);
                mFlingFromTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeFlingFromTarget, mFlingFromTarget);
                mScrollOffset = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollOffset, mScrollOffset);
                mScrollTouchUp = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollTouchUp, mScrollTouchUp);
            }
            array.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams params) {
            super(params);
        }

        public LayoutParams(MarginLayoutParams params) {
            super(params);
        }
    }


    public static class DefaultStopTargetViewFlingImpl implements StopTargetViewFlingImpl {

        private static DefaultStopTargetViewFlingImpl sInstance;

        public static DefaultStopTargetViewFlingImpl getInstance() {
            if (sInstance == null) sInstance = new DefaultStopTargetViewFlingImpl();
            return sInstance;
        }

        private DefaultStopTargetViewFlingImpl() {

        }

        @Override
        public void stopFling(View view) {
            if (view instanceof RecyclerView) ((RecyclerView) view).stopScroll();
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // 辅助方法
    ///////////////////////////////////////////////////////////////////////////
    private boolean isStartNestedScrollHorizontal(@ViewCompat.ScrollAxis int axes) {
        return axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && (isDirectionEnabled(Direction.LEFT) || isDirectionEnabled(Direction.RIGHT));
    }

    private boolean isStartNestedScrollVertical(@ViewCompat.ScrollAxis int axes) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL && (isDirectionEnabled(Direction.TOP) || isDirectionEnabled(Direction.BOTTOM));
    }

    private void checkScroll() {
        if (mTargetView == null) return;
        mScroller.abortAnimation();
        final int offsetX = mTargetViewOffsetHelper.getLeftAndRightOffset();
        final int offsetY = mTargetViewOffsetHelper.getTopAndBottomOffset();
        if (checkScrollHorizontal(getDirectionEdge(Direction.LEFT), offsetX, offsetY, 1)) return;
        if (checkScrollHorizontal(getDirectionEdge(Direction.RIGHT), offsetX, offsetY, -1)) return;
        if (checkScrollVertical(getDirectionEdge(Direction.TOP), offsetX, offsetY, 1)) return;
        if (checkScrollVertical(getDirectionEdge(Direction.BOTTOM), offsetX, offsetY, -1)) return;
        mState = STATE_IDLE;
    }

    /*1 left  -1 right*/
    private boolean checkScrollHorizontal(@Nullable final Edge edge, @Px final int offsetX, @Px final int offsetY, final int direction) {
        if (edge == null || direction * offsetX >= 0) return false;
        mState = STATE_SETTLING_TO_INIT_OFFSET;
        final int offset = Math.abs(offsetX);
        final int targetOffset = edge.getTargetOffset();
        if (offset == targetOffset) {
            edgeStart(edge);
            return true;
        }
        if (offset > targetOffset && !edge.isScrollTouchUp()) {
            edgeStart(edge);
            return true;
        }
        int scrollTarget = 0;
        if (offset > targetOffset && !edge.isScrollOffset()) {
            edgeStart(edge);
            scrollTarget = direction * targetOffset;
        } else if (offset > targetOffset) {
            mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
            scrollTarget = direction * targetOffset;
        }
        final int dx = scrollTarget - offsetX;
        mScroller.startScroll(offsetX, offsetY, dx, 0, scrollDuration(edge, dx));
        postInvalidateOnAnimation();
        return true;
    }

    /*1 top  -1 bottom*/
    private boolean checkScrollVertical(@Nullable final Edge edge, @Px final int offsetX, @Px final int offsetY, final int direction) {
        if (edge == null || direction * offsetY <= 0) return false;
        mState = STATE_SETTLING_TO_INIT_OFFSET;
        final int offset = Math.abs(offsetY);
        final int targetOffset = edge.getTargetOffset();
        if (offset == targetOffset) {
            edgeStart(edge);
            return true;
        }
        if (offset > targetOffset && !edge.isScrollTouchUp()) {
            edgeStart(edge);
            return true;
        }
        int scrollTarget = 0;
        if (offset > targetOffset && !edge.isScrollOffset()) {
            edgeStart(edge);
            scrollTarget = direction * targetOffset;
        } else if (offset > targetOffset) {
            mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
            scrollTarget = direction * targetOffset;
        }
        final int dy = scrollTarget - offsetY;
        mScroller.startScroll(offsetX, offsetY, 0, dy, scrollDuration(edge, dy));
        postInvalidateOnAnimation();
        return true;
    }

    private void checkFinal(@Nullable Edge edge, int finalX, int finalY) {
        if (edge != null && edge.isScrollFinal(finalX, finalY)) edgeStart(edge);
    }

    private void edgeUpdateOffset(final int currentX, final int currentY) {
        mTargetViewOffsetHelper.setOrientationOffset(Orientation.HORIZONTAL, currentX);
        mTargetViewOffsetHelper.setOrientationOffset(Orientation.VERTICAL, currentY);
        if (currentX >= 0) edgeUpdateOffset(getDirectionEdge(Direction.LEFT), currentX);
        if (currentY >= 0) edgeUpdateOffset(getDirectionEdge(Direction.TOP), currentY);
        if (currentX <= 0) edgeUpdateOffset(getDirectionEdge(Direction.RIGHT), currentX);
        if (currentY <= 0) edgeUpdateOffset(getDirectionEdge(Direction.BOTTOM), currentY);
    }


    private void edgeUpdateOffset(@Nullable final Edge edge, final int offset) {
        if (edge == null) return;
        edge.updateOffset(offset);
    }

    private void scrollToTargetOffset(@NonNull Edge edge, @Orientation int orientation, int offset) {
        mTargetViewOffsetHelper.setOrientationOffset(orientation, offset);
        edge.scrollToTarget(offset);
    }


    private void removeStopTargetFlingRunnable() {
        if (mStopTargetFlingRunnable != null) {
            removeCallbacks(mStopTargetFlingRunnable);
            mStopTargetFlingRunnable = null;
        }
    }

    private void stopTargetFling(@NonNull final View targetView, int dx, int dy, @ViewCompat.NestedScrollType int type) {
        if (mStopTargetFlingRunnable != null || type == ViewCompat.TYPE_TOUCH) return;
        if ((dy < 0 && !mTargetView.canScrollVertically(-1)) || (dy > 0 && !mTargetView.canScrollVertically(1)) ||
                (dx < 0 && !mTargetView.canScrollHorizontally(-1)) || (dx > 0 && !mTargetView.canScrollHorizontally(1))) {
            mStopTargetFlingRunnable = () -> {
                mStopTargetViewFlingImpl.stopFling(targetView);
                mStopTargetFlingRunnable = null;
                checkScroll();
            };
            post(mStopTargetFlingRunnable);
        }
    }

    private int getScroll(@Nullable Edge edge, @Orientation int orientation, int direction, @Px int scroll, @NonNull int[] consumed, @ViewCompat.NestedScrollType int type) {
        if (edge == null) return scroll;
        int offset = mTargetViewOffsetHelper.getOffset(orientation);
        if (scroll * direction < 0 && offset * direction < 0) {
            final float edgeRate = edge.getEdgeRate(type);
            int edgeScroll = (int) (scroll * edgeRate);
            if (edgeScroll == 0) return scroll;
            if (Math.abs(offset) >= Math.abs(edgeScroll)) {
                consumed[orientation] += scroll;
                offset -= edgeScroll;
                scroll = 0;
            } else {
                edgeScroll = (int) (offset / edgeRate);
                consumed[orientation] += edgeScroll;
                scroll -= edgeScroll;
                offset = 0;
            }
            scrollToTargetOffset(edge, orientation, offset);
        }
        return scroll;
    }

    private int getFling(@Nullable Edge edge, @Orientation int orientation, int direction, @Px int fling, int[] consumed, @ViewCompat.NestedScrollType int type) {
        if (edge == null) return fling;
        if (fling * direction > 0 && edge.isFlingFromTarget(type) && !mTargetView.canScrollVertically(direction)) {
            int offset = mTargetViewOffsetHelper.getOffset(orientation);
            final float rate = edge.getFlingRate(type, offset);
            int edgeFling = (int) (fling * rate);
            if (edgeFling == 0) return fling;
            if (edge.isEdgeOver() || edgeFling * direction <= edge.getTargetOffset() - offset) {
                offset -= edgeFling;
                consumed[orientation] += fling;
                fling = 0;
            } else {
                edgeFling = (int) ((offset - edge.getTargetOffset()) / rate);
                consumed[orientation] += edgeFling;
                fling -= edgeFling;
                offset = edge.getTargetOffset();
            }
            scrollToTargetOffset(edge, orientation, offset);
        }
        return fling;
    }

    /*left -1    right 1*/
    private boolean onNestedPreFlingHorizontally(@Nullable Edge edge, int offsetX, int offsetY, float velocity, int direction) {
        if (edge == null) return false;
        if (velocity * direction > 0 && !mTargetView.canScrollHorizontally(direction)) {
            mState = STATE_SETTLING_FLING;
            velocity /= mNestedPreFlingVelocityScaleDown;
            final int maxH = edge.getTargetOffsetMax();
            final int minH = edge.getTargetOffsetMin();
            mScroller.fling(offsetX, offsetY, (int) -velocity, 0, minH, maxH, offsetY, offsetY);
            postInvalidateOnAnimation();
            return true;
        } else if (velocity * direction < 0 && offsetX * direction < 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET;
            mScroller.startScroll(offsetX, offsetY, -offsetX, 0, scrollDuration(edge, offsetX));
            postInvalidateOnAnimation();
            return true;
        }
        return false;
    }

    private boolean onNestedPreFlingVertically(@Nullable Edge edge, int offsetH, int offsetV, float velocity, int direction) {
        if (edge == null) return false;
        if (velocity * direction > 0 && !mTargetView.canScrollVertically(direction)) {
            mState = STATE_SETTLING_FLING;
            velocity /= mNestedPreFlingVelocityScaleDown;
            final int maxV = edge.getTargetOffsetMax();
            final int minV = edge.getTargetOffsetMin();
            mScroller.fling(offsetH, offsetV, 0, (int) -velocity, offsetH, offsetH, minV, maxV);
            postInvalidateOnAnimation();
            return true;
        } else if (velocity * direction < 0 && offsetV * direction < 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET;
            mScroller.startScroll(offsetH, offsetV, 0, -offsetV, scrollDuration(edge, offsetV));
            postInvalidateOnAnimation();
            return true;
        }
        return false;
    }

    private int scrollDuration(@NonNull Edge edge, @Px int offset) {
        return Math.max(mMinScrollDuration, Math.abs(edge.getScrollOffset(offset)));
    }


}
