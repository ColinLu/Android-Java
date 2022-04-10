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

import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.annotation.Orientation;


public class EdgeLayout extends FrameLayout implements NestedScrollingParent3 {
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
    private ViewOffsetHelper mViewOffsetHelper;
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
        mDirectionEnabled = array.getInt(R.styleable.EdgeLayout_direction, Direction.LEFT | Direction.TOP | Direction.RIGHT | Direction.BOTTOM);
        array.recycle();
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mScroller = new OverScroller(context, INTERPOLATOR);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int count = getChildCount();
        if (count == 0) return;
        boolean isTargetSet = false;
        int edgesSet = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.mTarget) {
                if (isTargetSet) throw new RuntimeException("target view more than one");
                isTargetSet = true;
                setTargetView(child);
            } else {
                if ((edgesSet & lp.mDirection) != 0)
                    throw new RuntimeException("same direction edge view more than one");
                edgesSet |= lp.mDirection;
                setEdgeView(child, lp);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int w = r - l;
        final int h = b - t;
        if (mTargetView != null) {
            mTargetView.layout(0, 0, w, h);
            mViewOffsetHelper.onViewLayout();
        }
        if (mEdgeLeft != null) mEdgeLeft.onLayout(w, h);
        if (mEdgeTop != null) mEdgeTop.onLayout(w, h);
        if (mEdgeRight != null) mEdgeRight.onLayout(w, h);
        if (mEdgeBottom != null) mEdgeBottom.onLayout(w, h);
    }


    @Override
    public void computeScroll() {
        if (!mScroller.computeScrollOffset()) return;
        if (mState == STATE_TRIGGERING) return;
        if (mState == STATE_SETTLING_TO_INIT_OFFSET) {
            mState = STATE_IDLE;
            return;
        }
        if (mState == STATE_SETTLING_FLING) {
            scrollToTargetOffset();
            return;
        }
        final Edge left = getDirectionEdge(Direction.LEFT);
        final Edge top = getDirectionEdge(Direction.TOP);
        final Edge right = getDirectionEdge(Direction.RIGHT);
        final Edge bottom = getDirectionEdge(Direction.BOTTOM);
        if (!mScroller.isFinished()) {
            final int offsetX = mScroller.getCurrX();
            final int offsetY = mScroller.getCurrY();
            setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.LEFT), Orientation.HORIZONTAL, -1, offsetX);
            setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.TOP), Orientation.VERTICAL, -1, offsetY);
            setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.RIGHT), Orientation.HORIZONTAL, 1, offsetX);
            setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.BOTTOM), Orientation.VERTICAL, 1, offsetY);
            postInvalidateOnAnimation();
            return;
        }

        if (mState == STATE_SETTLING_TO_TRIGGER_OFFSET) {
            final int finalX = mScroller.getFinalX();
            final int finalY = mScroller.getFinalY();
            edgeStart(getDirectionEdge(Direction.LEFT), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.TOP), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.RIGHT), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.BOTTOM), finalX, finalY);
            updateOffset(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {
        return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes, @ViewCompat.NestedScrollType int type) {
        return mTargetView == target
                && (axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && (isDirectionEnabled(Direction.LEFT) || isDirectionEnabled(Direction.RIGHT))) ||
                (axes == ViewCompat.SCROLL_AXIS_VERTICAL && (isDirectionEnabled(Direction.TOP) || isDirectionEnabled(Direction.BOTTOM)));
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
    public void onStopNestedScroll(@NonNull View target, @ViewCompat.NestedScrollType int type) {
        if (mState == STATE_PULLING) {
            scrollToTargetOffset();
        } else if (mState == STATE_SETTLING_DELIVER && type != ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable();
            scrollToTargetOffset();
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
        final int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        final int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
        // if the targetView is RecyclerView and we set OnFlingListener for RecyclerView.
        // then the targetView can not deliver fling consume to NestedScrollParent
        // so we intercept the fling if the target view can not consume the fling.
        if (onNestedPreFlingHorizontally(getDirectionEdge(Direction.LEFT), -1, velocityX, velocityY, hOffset, vOffset)) {
            return true;
        }
        if (onNestedPreFlingHorizontally(getDirectionEdge(Direction.RIGHT), 1, velocityX, velocityY, hOffset, vOffset)) {
            return true;
        }
        if (onNestedPreFlingVertically(getDirectionEdge(Direction.TOP), -1, velocityX, velocityY, hOffset, vOffset)) {
            return true;
        }
        if (onNestedPreFlingVertically(getDirectionEdge(Direction.BOTTOM), 1, velocityX, velocityY, hOffset, vOffset)) {
            return true;
        }
        mState = STATE_SETTLING_DELIVER;
        return super.onNestedPreFling(target, velocityX, velocityY);
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


    public void setTargetView(@NonNull View view) {
        if (view.getParent() != this) {
            throw new RuntimeException("Target already exists other parent view.");
        }
        if (view.getParent() == null) {
            final int match = ViewGroup.LayoutParams.MATCH_PARENT;
            final LayoutParams lp = new LayoutParams(match, match);
            addView(view, lp);
        }
        mTargetView = view;
        mViewOffsetHelper = new ViewOffsetHelper(view);
    }

    public void setEdgeView(@NonNull View view, @NonNull LayoutParams params) {
        final Edge.Builder builder = new Edge.Builder(view, params.mDirection)
                .setEdgeOver(params.mEdgeOver)
                .setFlingFromTarget(params.mFlingFromTarget)
                .setScrollOffset(params.mScrollOffset)
                .setScrollTouchUp(params.mScrollTouchUp)
                .setStartOffset(params.mStartOffset)
                .setTargetOffset(params.mTargetOffset)
                .setEdgeRate(params.mScrollSpeed)
                .setScrollFling(params.mScrollSpeed)
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

    private void edgeStart(@Nullable Edge edge, int finalX, int finalY) {
        if (edge != null && edge.getScrollFinal(finalX, finalY)) {
            edgeStart(edge, STATE_TRIGGERING);
        }
    }

    private void edgeStart(@NonNull Edge edge, int state) {
        this.mState = state;
        edgeStart(edge);
    }

    public void edgeStart(@NonNull Edge edge) {
        if (edge.isRunning()) return;
        edge.setRunning(true);
        if (mOnEdgeListener != null) mOnEdgeListener.start(edge);
        if (edge.getView() instanceof EdgeWatcher) ((EdgeWatcher) edge.getView()).start();
    }


    public void edgeFinish() {
        if (mEdgeLeft != null && mEdgeLeft.isRunning()) edgeFinish(mEdgeLeft, true);
        if (mEdgeTop != null && mEdgeTop.isRunning()) edgeFinish(mEdgeTop, true);
        if (mEdgeRight != null && mEdgeRight.isRunning()) edgeFinish(mEdgeRight, true);
        if (mEdgeBottom != null && mEdgeBottom.isRunning()) edgeFinish(mEdgeBottom, true);
    }

    public void edgeFinish(@NonNull Edge edge) {
        edgeFinish(edge, true);
    }

    public void edgeFinish(@NonNull Edge edge, boolean animate) {
        edge.setRunning(false);
        if (edge.getView() instanceof EdgeWatcher) ((EdgeWatcher) edge.getView()).finish();
        if (mState == STATE_PULLING) return;
        if (!animate) {
            mState = STATE_IDLE;
            updateOffset(0, 0);
            return;
        }
        mState = STATE_SETTLING_TO_INIT_OFFSET;
        final int direction = edge.getDirection();
        final int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
        final int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        if (direction == Direction.TOP && vOffset > 0) {
            mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(edge, vOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.BOTTOM && vOffset < 0) {
            mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(edge, vOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.LEFT && hOffset > 0) {
            mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(edge, hOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.RIGHT && hOffset < 0) {
            mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(edge, hOffset));
            postInvalidateOnAnimation();
        }
    }


    private void scrollToTargetOffset() {
        if (mTargetView == null) return;
        mScroller.abortAnimation();
        final int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        final int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
        if (scrollToTargetOffsetHorizontally(getDirectionEdge(Direction.LEFT), -1, hOffset, vOffset)) {
            return;
        }
        if (scrollToTargetOffsetHorizontally(getDirectionEdge(Direction.RIGHT), 1, hOffset, vOffset)) {
            return;
        }
        if (scrollToTargetOffsetVertically(getDirectionEdge(Direction.TOP), -1, hOffset, vOffset)) {
            return;
        }
        if (scrollToTargetOffsetVertically(getDirectionEdge(Direction.BOTTOM), 1, hOffset, vOffset)) {
            return;
        }
        mState = STATE_IDLE;
    }

    private boolean scrollToTargetOffsetHorizontally(@Nullable final Edge edge, final int direction, final int offsetH, final int offsetV) {
        if (edge == null || offsetH * direction >= 0) return false;
        final int targetOffset = edge.getTargetOffset();
        if (targetOffset == offsetH) {
            edgeStart(edge, STATE_TRIGGERING);
            return true;
        }
        int scrollTarget = 0;
        if (Math.abs(offsetH) > targetOffset) {
            if (!edge.isScrollTouchUp()) {
                edgeStart(edge, STATE_TRIGGERING);
                return true;
            }
            if (!edge.isScrollOffset()) edgeStart(edge, STATE_TRIGGERING);
            else mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
            scrollTarget = -targetOffset * direction;
        } else mState = STATE_SETTLING_TO_INIT_OFFSET;
        int dx = scrollTarget - offsetH;
        mScroller.startScroll(offsetH, offsetV, dx, 0, scrollDuration(edge, dx));
        postInvalidateOnAnimation();
        return true;
    }

    private boolean scrollToTargetOffsetVertically(@Nullable final Edge edge, final int direction, final int offsetH, final int offsetV) {
        if (edge == null || offsetV * direction >= 0) return false;
        final int targetOffset = edge.getTargetOffset();
        if (targetOffset == offsetV) {
            edgeStart(edge, STATE_TRIGGERING);
            return true;
        }
        int scrollTarget = 0;
        if (Math.abs(offsetV) > targetOffset) {
            if (!edge.isScrollTouchUp()) {
                edgeStart(edge, STATE_TRIGGERING);
                return true;
            }
            if (!edge.isScrollOffset()) edgeStart(edge, STATE_TRIGGERING);
            else mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
            scrollTarget = -targetOffset * direction;
        } else mState = STATE_SETTLING_TO_INIT_OFFSET;
        int dy = scrollTarget - offsetV;
        mScroller.startScroll(offsetH, offsetV, offsetH, dy, scrollDuration(edge, dy));
        postInvalidateOnAnimation();
        return true;
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
                scrollToTargetOffset();
            };
            post(mStopTargetFlingRunnable);
        }
    }

    private int getScroll(@Nullable Edge edge, @Orientation int orientation, int direction, @Px int scroll, @NonNull int[] consumed, @ViewCompat.NestedScrollType int type) {
        if (edge == null) return scroll;
        int offset = mViewOffsetHelper.getOffset(orientation);
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
            setOffsetToTargetOffsetHelper(edge, orientation, direction, offset);
        }
        return scroll;
    }

    private int getFling(@Nullable Edge edge, @Orientation int orientation, int direction, @Px int fling, int[] consumed, @ViewCompat.NestedScrollType int type) {
        if (edge == null) return fling;
        if (fling * direction > 0 && edge.isFlingFromTarget(type) && !mTargetView.canScrollVertically(direction)) {
            int offset = mViewOffsetHelper.getOffset(orientation);
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
            setOffsetToTargetOffsetHelper(edge, orientation, direction, offset);
        }
        return fling;
    }

    private void updateOffset(int offsetX, int offsetY) {
        setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.LEFT), Orientation.HORIZONTAL, -1, offsetX);
        setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.TOP), Orientation.VERTICAL, -1, offsetY);
        setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.RIGHT), Orientation.HORIZONTAL, 1, offsetX);
        setOffsetToTargetOffsetHelper(getDirectionEdge(Direction.BOTTOM), Orientation.VERTICAL, 1, offsetY);
    }

    private void setOffsetToTargetOffsetHelper(@Nullable Edge edge, @Orientation int orientation, int direction, int offset) {
        if (edge == null) return;
        mViewOffsetHelper.setOrientationOffset(orientation, offset);
        if (orientation == Orientation.VERTICAL) mViewOffsetHelper.setTopAndBottomOffset(offset);
        else mViewOffsetHelper.setLeftAndRightOffset(offset);
        edge.onTargetMoved(-direction * offset);
        if (edge.getView() instanceof EdgeWatcher) {
            ((EdgeWatcher) edge.getView()).offset(edge, offset);
        }
    }

    private boolean onNestedPreFlingHorizontally(@Nullable Edge edge, int direction, float velocityX, float velocityY, int offsetH, int offsetV) {
        if (edge == null) return false;
        if (velocityX * direction > 0 && !mTargetView.canScrollHorizontally(direction)) {
            mState = STATE_SETTLING_FLING;
            velocityX /= mNestedPreFlingVelocityScaleDown;
            final int maxH = edge.getTargetOffsetMax();
            final int minH = edge.getTargetOffsetMin();
            mScroller.fling(offsetH, offsetV, (int) -velocityX, (int) velocityY, minH, maxH, offsetV, offsetV);
            postInvalidateOnAnimation();
            return true;
        } else if (velocityX * direction < 0 && offsetH > 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET;
            mScroller.startScroll(offsetH, offsetV, -offsetH, (int) velocityY, scrollDuration(edge, offsetH));
            postInvalidateOnAnimation();
            return true;
        }
        return false;
    }

    private boolean onNestedPreFlingVertically(@Nullable Edge edge, int direction, float velocityX, float velocityY, int offsetH, int offsetV) {
        if (edge == null) return false;
        if (velocityY * direction > 0 && !mTargetView.canScrollVertically(direction)) {
            mState = STATE_SETTLING_FLING;
            velocityY /= mNestedPreFlingVelocityScaleDown;
            final int maxV = edge.getTargetOffsetMax();
            final int minV = edge.getTargetOffsetMin();
            mScroller.fling(offsetH, offsetV, 0, (int) -velocityY, offsetH, offsetH, minV, maxV);
            postInvalidateOnAnimation();
            return true;
        } else if (velocityY * direction < 0 && offsetV > 0) {
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
            if (sInstance == null) {
                sInstance = new DefaultStopTargetViewFlingImpl();
            }
            return sInstance;
        }

        private DefaultStopTargetViewFlingImpl() {

        }

        @Override
        public void stopFling(View view) {
            if (view instanceof RecyclerView) {
                ((RecyclerView) view).stopScroll();
            }
        }
    }

    public interface OnEdgeListener {
        default void start(@NonNull Edge edge) {
        }

        default void offset(@Direction int direction, int offset) {
        }

        default void finish(@NonNull Edge edge) {
        }
    }


    public interface StopTargetViewFlingImpl {
        void stopFling(View view);
    }
}
