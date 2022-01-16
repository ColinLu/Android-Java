package com.colin.library.android.widgets.edge;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.annotation.Direction;


public class EdgeLayout extends FrameLayout implements NestedScrollingParent3, NestedScrollingChild3 {
    /*停止*/
    public interface StopTargetViewFling {
        void stopFling(View view);
    }

    public static final Interpolator INTERPOLATOR_DEFAULT = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };
    private static final int STATE_IDLE = 0;
    private static final int STATE_PULLING = 1;
    private static final int STATE_SETTLING_TO_TRIGGER_OFFSET = 2;
    private static final int STATE_TRIGGERING = 3;
    private static final int STATE_SETTLING_TO_INIT_OFFSET = 4;
    private static final int STATE_SETTLING_DELIVER = 5;
    private static final int STATE_SETTLING_FLING = 6;


    private int mEnabledEdges;
    private View mTargetView;
    private Edge mLeftEdge = null;
    private Edge mTopEdge = null;
    private Edge mRightEdge = null;
    private Edge mBottomEdge = null;

    private ViewOffsetHelper mOffsetHelper;
    private OnEdgeListener mOnEdgeListener;

    private StopTargetViewFling mStopTargetViewFling = DefaultStopTargetViewFlingImpl.getInstance();
    private Runnable mStopTargetFlingRunnable = null;
    private float mNestedPreFlingVelocityScaleDown = 10;
    private int mMinScrollDuration = Constants.DURATION_SCROLL_MIN;
    private int mState = STATE_IDLE;

    private final int[] mNestedScrollingV2ConsumedCompat = new int[2];
    private final OverScroller mScroller;
    private final NestedScrollingParentHelper mParentHelper;
    private final NestedScrollingChildHelper mChildHelper;

    public EdgeLayout(@NonNull Context context) {
        this(context, null);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final int all = Direction.LEFT | Direction.TOP | Direction.RIGHT | Direction.BOTTOM;
        mScroller = new OverScroller(context, INTERPOLATOR_DEFAULT);
        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout, defStyleAttr, 0);
            mEnabledEdges = array.getInteger(R.styleable.EdgeLayout_direction, all);
            array.recycle();
        } else mEnabledEdges = all;
        setNestedScrollingEnabled(true);
    }

    /*布局完了*/
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            final View view = getChildAt(i);
            final LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params.mTarget) setTargetView(view);
            else setEdgeView(view, params);
        }
    }

    @Override
    public void computeScroll() {
        if (!mScroller.computeScrollOffset()) return;
        if (mScroller.isFinished()) {
            if (mState == STATE_SETTLING_TO_INIT_OFFSET) {
                mState = STATE_IDLE;
                return;
            }
            if (mState == STATE_TRIGGERING) return;

            if (mState == STATE_SETTLING_FLING) {
                checkScrollToTargetOffsetOrInitOffset();
                return;
            }
            if (mState == STATE_SETTLING_TO_TRIGGER_OFFSET) {
                mState = STATE_TRIGGERING;
                edgeStart(mScroller.getFinalX(), mScroller.getFinalY());
                setHorOffsetToTargetOffsetHelper(mScroller.getCurrX());
                setVerOffsetToTargetOffsetHelper(mScroller.getCurrY());
            }
        } else {
            setHorOffsetToTargetOffsetHelper(mScroller.getCurrX());
            setVerOffsetToTargetOffsetHelper(mScroller.getCurrY());
            postInvalidateOnAnimation();
        }
    }

    private void edgeStart(final int x, final int y) {
        if (Math.abs(x) > Math.abs(y)) {//横向
            if (x > 0) startEdge(getEdge(Direction.LEFT), x);
            else if (x < 0) startEdge(getEdge(Direction.RIGHT), -x);
        } else {//纵向
            if (y > 0) startEdge(getEdge(Direction.TOP), y);
            else if (y < 0) startEdge(getEdge(Direction.BOTTOM), -y);
        }
    }


    private void startEdge(@Nullable Edge edge, @Px int offset) {
        if (edge != null && !edge.isRunning() && isEdgeEnabled(edge.getDirection()) && edge.getTargetOffset() == offset) {
            final View view = edge.getView();
            if (view instanceof EdgeWatcher) ((EdgeWatcher) view).start();
            if (mOnEdgeListener != null) mOnEdgeListener.edge(edge);
        }
    }

    private void checkScrollToTargetOffsetOrInitOffset() {
        if (mTargetView == null) return;
        mScroller.abortAnimation();
        final int hOffset = mOffsetHelper.getLeftAndRightOffset();
        final int vOffset = mOffsetHelper.getTopAndBottomOffset();
        int hTarget = 0, vTarget = 0;
        //水平
        if (Math.abs(hOffset) > Math.abs(vOffset)) {
            //左
            if (hOffset > 0 && isEdgeEnabled(Direction.LEFT) && mLeftEdge != null) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                final int targetOffset = mLeftEdge.getTargetOffset();
                if (targetOffset == hOffset) {
                    startEdge(mLeftEdge, targetOffset);
                    return;
                }
                if (targetOffset < hOffset) {
                    if (!mLeftEdge.isScrollTouchUp()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mLeftEdge, targetOffset);
                        return;
                    }
                    if (!mLeftEdge.isScrollOffset()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mLeftEdge, targetOffset);
                    } else mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                    hTarget = targetOffset;
                }

                int dx = hTarget - hOffset;
                mScroller.startScroll(hOffset, vOffset, dx, 0, scrollDuration(mLeftEdge, dx));
                postInvalidateOnAnimation();
                return;
            }
            //右
            if (hOffset < 0 && isEdgeEnabled(Direction.RIGHT) && mRightEdge != null) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                final int targetOffset = mRightEdge.getTargetOffset();
                if (targetOffset == -hOffset) {
                    startEdge(mRightEdge, targetOffset);
                    return;
                }
                if (targetOffset < -hOffset) {
                    if (!mRightEdge.isScrollTouchUp()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mRightEdge, targetOffset);
                        return;
                    }
                    if (!mRightEdge.isScrollOffset()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mRightEdge, targetOffset);
                    } else mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                    hTarget = -targetOffset;
                }

                int dx = hTarget - hOffset;
                mScroller.startScroll(hOffset, vOffset, dx, 0, scrollDuration(mRightEdge, dx));
                postInvalidateOnAnimation();
                return;
            }
        }

        //垂直
        if (Math.abs(vOffset) > Math.abs(hOffset)) {
            //上
            if (vOffset > 0 && isEdgeEnabled(Direction.TOP) && mTopEdge != null) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                final int targetOffset = mTopEdge.getTargetOffset();
                if (targetOffset == vOffset) {
                    startEdge(mTopEdge, targetOffset);
                    return;
                }
                if (targetOffset < vOffset) {
                    if (!mTopEdge.isScrollTouchUp()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mTopEdge, targetOffset);
                        return;
                    }
                    if (!mTopEdge.isScrollOffset()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mTopEdge, targetOffset);
                    } else mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                    vTarget = targetOffset;
                }

                int dy = vTarget - vOffset;
                mScroller.startScroll(hOffset, vOffset, hOffset, dy, scrollDuration(mTopEdge, dy));
                postInvalidateOnAnimation();
                return;
            }
            //下
            if (vOffset < 0 && isEdgeEnabled(Direction.BOTTOM) && mBottomEdge != null) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                final int targetOffset = mBottomEdge.getTargetOffset();
                if (targetOffset == -vOffset) {
                    startEdge(mBottomEdge, targetOffset);
                    return;
                }
                if (targetOffset < -vOffset) {
                    if (!mBottomEdge.isScrollTouchUp()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mBottomEdge, targetOffset);
                        return;
                    }
                    if (!mBottomEdge.isScrollOffset()) {
                        mState = STATE_TRIGGERING;
                        startEdge(mBottomEdge, targetOffset);
                    } else mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                    vTarget = targetOffset;
                }

                int dy = vTarget - vOffset;
                mScroller.startScroll(hOffset, vOffset, hOffset, dy, scrollDuration(mBottomEdge, dy));
                postInvalidateOnAnimation();
                return;
            }
        }
        mState = STATE_IDLE;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int w = r - l;
        final int h = b - t;
        if (mTargetView != null) {
            mTargetView.layout(0, 0, w, h);
            mOffsetHelper.onViewLayout();
        }
        if (mLeftEdge != null) mLeftEdge.onLayout(w, h, l, t, r, b);
        if (mTopEdge != null) mTopEdge.onLayout(w, h, l, t, r, b);
        if (mRightEdge != null) mRightEdge.onLayout(w, h, l, t, r, b);
        if (mBottomEdge != null) mBottomEdge.onLayout(w, h, l, t, r, b);

    }

    public void setNestedPreFlingVelocityScaleDown(float nestedPreFlingVelocityScaleDown) {
        mNestedPreFlingVelocityScaleDown = nestedPreFlingVelocityScaleDown;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return mTargetView == target && (axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && (isEdgeEnabled(Direction.LEFT) || isEdgeEnabled(Direction.RIGHT))) ||
                (axes == ViewCompat.SCROLL_AXIS_VERTICAL && (isEdgeEnabled(Direction.TOP) || isEdgeEnabled(Direction.BOTTOM)));
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable();
            mScroller.abortAnimation();
            mState = STATE_PULLING;
        }
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }


    @Override
    public void onNestedPreScroll(@NonNull final View target, int dx, int dy, @NonNull int[] consumed, int type) {
        int originDx = dx, originDy = dy;
        dy = checkEdgeTopScrollDown(dy, consumed, type);
        dy = checkEdgeBottomScrollDown(dy, consumed, type);
        dy = checkEdgeTopScrollUp(dy, consumed, type);
        dy = checkEdgeBottomScrollUp(dy, consumed, type);

        dx = checkEdgeLeftScrollRight(dx, consumed, type);
        dx = checkEdgeRightScrollRight(dx, consumed, type);
        dx = checkEdgeLeftScrollLeft(dx, consumed, type);
        dx = checkEdgeRightScrollLeft(dx, consumed, type);

        if (originDx == dx && originDy == dy && mState == STATE_SETTLING_DELIVER) {
            checkStopTargetFling(target, dx, dy, type);
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        int originDxUnconsumed = dxUnconsumed, originDyUnconsumed = dyUnconsumed;
        dyUnconsumed = checkEdgeTopScrollDown(dyUnconsumed, consumed, type);
        dyUnconsumed = checkEdgeBottomScrollDown(dyUnconsumed, consumed, type);
        dyUnconsumed = checkEdgeTopScrollUp(dyUnconsumed, consumed, type);
        dyUnconsumed = checkEdgeBottomScrollUp(dyUnconsumed, consumed, type);

        dxUnconsumed = checkEdgeLeftScrollRight(dxUnconsumed, consumed, type);
        dxUnconsumed = checkEdgeRightScrollRight(dxUnconsumed, consumed, type);
        dxUnconsumed = checkEdgeLeftScrollLeft(dxUnconsumed, consumed, type);
        dxUnconsumed = checkEdgeRightScrollLeft(dxUnconsumed, consumed, type);
        if (dyUnconsumed == originDyUnconsumed && dxUnconsumed == originDxUnconsumed && mState == STATE_SETTLING_DELIVER) {
            checkStopTargetFling(target, dxUnconsumed, dyUnconsumed, type);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, mNestedScrollingV2ConsumedCompat);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        int hOffset = mOffsetHelper.getLeftAndRightOffset();
        int vOffset = mOffsetHelper.getTopAndBottomOffset();

        if (mLeftEdge != null && isEdgeEnabled(Direction.LEFT)) {
            if (velocityX < 0 && !mTargetView.canScrollHorizontally(-1)) {
                mState = STATE_SETTLING_FLING;
                velocityX /= mNestedPreFlingVelocityScaleDown;
                int maxX = mLeftEdge.isEdgeOver() ? Integer.MAX_VALUE : mLeftEdge.getTargetOffset();
                mScroller.fling(hOffset, vOffset, (int) -velocityX, 0, 0, maxX, vOffset, vOffset);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityX > 0 && hOffset > 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(mLeftEdge, hOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }

        if (mRightEdge != null && isEdgeEnabled(Direction.RIGHT)) {
            if (velocityX > 0 && !mTargetView.canScrollHorizontally(1)) {
                mState = STATE_SETTLING_FLING;
                velocityX /= mNestedPreFlingVelocityScaleDown;
                int minX = mRightEdge.isEdgeOver() ? Integer.MIN_VALUE : -mRightEdge.getTargetOffset();
                mScroller.fling(hOffset, vOffset, (int) -velocityX, 0, minX, 0, vOffset, vOffset);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityX < 0 && hOffset < 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(mRightEdge, hOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }

        if (mTopEdge != null && isEdgeEnabled(Direction.TOP)) {
            if (velocityY < 0 && !mTargetView.canScrollVertically(-1)) {
                mState = STATE_SETTLING_FLING;
                velocityY /= mNestedPreFlingVelocityScaleDown;
                int maxY = mTopEdge.isEdgeOver() ? Integer.MAX_VALUE : mTopEdge.getTargetOffset();
                mScroller.fling(hOffset, vOffset, 0, (int) -velocityY, hOffset, hOffset, 0, maxY);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityY > 0 && vOffset > 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(mTopEdge, vOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }

        if (mBottomEdge != null && isEdgeEnabled(Direction.BOTTOM)) {
            if (velocityY > 0 && !mTargetView.canScrollVertically(1)) {
                mState = STATE_SETTLING_FLING;
                velocityY /= mNestedPreFlingVelocityScaleDown;
                int minY = mBottomEdge.isEdgeOver() ? Integer.MIN_VALUE : -mBottomEdge.getTargetOffset();
                mScroller.fling(hOffset, vOffset, 0, (int) -velocityY, hOffset, hOffset, minY, 0);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityY < 0 && vOffset < 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(mBottomEdge, vOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }
        mState = STATE_SETTLING_DELIVER;
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        if (mState == STATE_PULLING) {
            checkScrollToTargetOffsetOrInitOffset();
        } else if (mState == STATE_SETTLING_DELIVER && type != ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable();
            checkScrollToTargetOffsetOrInitOffset();
        }
    }


    /* Child 3*/
    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    /* Child 3*/

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
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


    ///////////////////////////////////////////////////////////////////////////
    // 对外公开方法
    ///////////////////////////////////////////////////////////////////////////
    public void setTargetView(@NonNull View view) {
        if (mTargetView != null) {
            throw new RuntimeException("More than one view in xml are marked by qmui_is_target = true.");
        }
        if (view.getParent() != null && view.getParent() != this) {
            throw new RuntimeException("Target already exists other parent view.");
        }
        if (view.getParent() == null) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.mTarget = true;
            addView(view, params);
        }
        mTargetView = view;
        mOffsetHelper = new ViewOffsetHelper(view);
    }

    public void setEdge(@Direction int direction, @NonNull View view) {
        LayoutParams params = new LayoutParams(view.getLayoutParams());
        params.mDirection = direction;
        setEdgeView(view, params);
    }

    public void setEdgeView(@NonNull View view, @NonNull LayoutParams params) {
        Edge.Builder builder = new Edge.Builder(view, params.mDirection)
                .setStartOffset(params.mEdgeStartOffset)
                .setTargetOffset(params.mEdgeTargetOffset)
                .setEdgeRate(params.mEdgeRate)
                .setEdgeOver(params.mEdgeOver)
                .setScrollFling(params.mEdgeScrollFling)
                .setScrollSpeed(params.mEdgeScrollSpeed)
                .setFlingFromTarget(params.mFlingFromTarget)
                .setScrollOffset(params.mEdgeScrollOffset)
                .setScrollTouchUp(params.mEdgeScrollTouchUp);
        view.setLayoutParams(params);
        setEdgeView(builder);
    }

    public void setEdgeView(@NonNull Edge.Builder builder) {
        if (builder.getView().getParent() != null && builder.getView().getParent() != this) {
            throw new RuntimeException("Action view already exists other parent view.");
        }
        Edge edgeView = getEdge(builder.getDirection());
        if (edgeView != null) {
            throw new RuntimeException("More than one view in xml marked by layout_edge = " + builder.getDirection());
        }

        if (builder.getView().getParent() == null) {
            ViewGroup.LayoutParams params = builder.getView().getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            addView(builder.getView(), params);
        }
        if (builder.getDirection() == Direction.LEFT) mLeftEdge = builder.build();
        else if (builder.getDirection() == Direction.TOP) mTopEdge = builder.build();
        else if (builder.getDirection() == Direction.RIGHT) mRightEdge = builder.build();
        else if (builder.getDirection() == Direction.BOTTOM) mBottomEdge = builder.build();

    }

    public void setTargetViewFling(@NonNull StopTargetViewFling stopTargetViewFling) {
        mStopTargetViewFling = stopTargetViewFling;
    }

    public boolean isRunning() {
        return (mLeftEdge != null && mLeftEdge.isRunning())
                || (mTopEdge != null && mTopEdge.isRunning())
                || (mRightEdge != null && mRightEdge.isRunning())
                || (mBottomEdge != null && mBottomEdge.isRunning());
    }

    public void setScrollDuration(int scrollDuration) {
        mMinScrollDuration = scrollDuration;
    }


    public void setOnEdgeListener(@Nullable OnEdgeListener onEdgeListener) {
        mOnEdgeListener = onEdgeListener;
    }

    public void setEnabledEdges(int enable) {
        mEnabledEdges = enable;
    }

    public boolean isEdgeEnabled(@Direction int direction) {
        return (mEnabledEdges & direction) == direction;
    }

    @Nullable
    private Edge getEdge(@Direction int direction) {
        if (direction == Direction.LEFT) return mLeftEdge;
        else if (direction == Direction.TOP) return mTopEdge;
        else if (direction == Direction.RIGHT) return mRightEdge;
        else if (direction == Direction.BOTTOM) return mBottomEdge;
        return null;
    }


    private int scrollDuration(@NonNull Edge edge, int delta) {
        return Math.max(mMinScrollDuration, Math.abs((int) (edge.getScrollSpeed() * delta)));
    }


    public void edgeFinish() {
        if (mLeftEdge != null && mLeftEdge.isRunning()) edgeFinish(mLeftEdge, true);
        if (mTopEdge != null && mTopEdge.isRunning()) edgeFinish(mTopEdge, true);
        if (mRightEdge != null && mRightEdge.isRunning()) edgeFinish(mRightEdge, true);
        if (mBottomEdge != null && mBottomEdge.isRunning())
            edgeFinish(mBottomEdge, true);
    }

    public void edgeFinish(@NonNull Edge edge) {
        edgeFinish(edge, true);
    }

    public void edgeFinish(@NonNull Edge edge, boolean animate) {
        if (edge != getEdge(edge.getDirection())) return;
        final View view = edge.getView();
        if (view instanceof EdgeWatcher) ((EdgeWatcher) view).finish();
        if (mState == STATE_PULLING) return;

        if (!animate) {
            mState = STATE_IDLE;
            setVerOffsetToTargetOffsetHelper(0);
            setHorOffsetToTargetOffsetHelper(0);
            return;
        }
        mState = STATE_SETTLING_TO_INIT_OFFSET;
        final int direction = edge.getDirection();
        final int vOffset = mOffsetHelper.getTopAndBottomOffset();
        final int hOffset = mOffsetHelper.getLeftAndRightOffset();
        if (direction == Direction.TOP && mTopEdge != null && vOffset > 0) {
            mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(mTopEdge, vOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.BOTTOM && mBottomEdge != null && vOffset < 0) {
            mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(mBottomEdge, vOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.LEFT && mLeftEdge != null && hOffset > 0) {
            mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(mLeftEdge, hOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.RIGHT && mRightEdge != null && hOffset < 0) {
            mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(mRightEdge, hOffset));
            postInvalidateOnAnimation();
        }
    }

    private void removeStopTargetFlingRunnable() {
        if (mStopTargetFlingRunnable != null) {
            removeCallbacks(mStopTargetFlingRunnable);
            mStopTargetFlingRunnable = null;
        }
    }

    private void checkStopTargetFling(final View targetView, int dx, int dy, int type) {
        if (mStopTargetFlingRunnable != null || type == ViewCompat.TYPE_TOUCH) return;

        if ((dy < 0 && !mTargetView.canScrollVertically(-1)) ||
                (dy > 0 && !mTargetView.canScrollVertically(1)) ||
                (dx < 0 && !mTargetView.canScrollHorizontally(-1)) ||
                (dx > 0 && !mTargetView.canScrollHorizontally(1))) {
            mStopTargetFlingRunnable = new Runnable() {
                @Override
                public void run() {
                    mStopTargetViewFling.stopFling(targetView);
                    mStopTargetFlingRunnable = null;
                    checkScrollToTargetOffsetOrInitOffset();
                }
            };
            post(mStopTargetFlingRunnable);
        }
    }

    private void setHorOffsetToTargetOffsetHelper(int hOffset) {
        mOffsetHelper.setLeftAndRightOffset(hOffset);
        onTargetViewLeftAndRightOffsetChanged(hOffset);
        final View leftView = null == mLeftEdge ? null : mLeftEdge.getView();
        final View rightView = null == mRightEdge ? null : mRightEdge.getView();
        if (leftView != null && leftView instanceof EdgeWatcher) {
            mLeftEdge.onTargetMoved(hOffset);
            ((EdgeWatcher) leftView).offset(mLeftEdge, hOffset);
        }

        if (rightView != null && rightView instanceof EdgeWatcher) {
            mRightEdge.onTargetMoved(-hOffset);
            ((EdgeWatcher) rightView).offset(mRightEdge, -hOffset);
        }
    }

    private void setVerOffsetToTargetOffsetHelper(int vOffset) {
        mOffsetHelper.setTopAndBottomOffset(vOffset);
        onTargetViewTopAndBottomOffsetChanged(vOffset);
        final View topView = null == mTopEdge ? null : mTopEdge.getView();
        final View bottomView = null == mBottomEdge ? null : mBottomEdge.getView();
        if (topView != null && topView instanceof EdgeWatcher) {
            mTopEdge.onTargetMoved(vOffset);
            ((EdgeWatcher) topView).offset(mTopEdge, vOffset);
        }

        if (bottomView != null && bottomView instanceof EdgeWatcher) {
            mBottomEdge.onTargetMoved(-vOffset);
            ((EdgeWatcher) bottomView).offset(mBottomEdge, -vOffset);
        }
    }

    protected void onTargetViewTopAndBottomOffsetChanged(int vOffset) {

    }

    protected void onTargetViewLeftAndRightOffsetChanged(int hOffset) {

    }

    private int checkEdgeTopScrollDown(int dy, int[] consumed, int type) {
        int vOffset = mOffsetHelper.getTopAndBottomOffset();
        if (dy > 0 && mTopEdge != null && isEdgeEnabled(Direction.TOP) && vOffset > 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mTopEdge.getEdgeRate() : 1f;
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (vOffset >= ry) {
                consumed[1] += dy;
                vOffset -= ry;
                dy = 0;
            } else {
                int yConsumed = (int) (vOffset / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = 0;
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeTopScrollUp(int dy, int[] consumed, int type) {
        if (dy < 0 && mTopEdge != null && isEdgeEnabled(Direction.TOP) && !mTargetView.canScrollVertically(-1) &&
                (type == ViewCompat.TYPE_TOUCH || mTopEdge.isFlingFromTarget())) {
            int vOffset = mOffsetHelper.getTopAndBottomOffset();
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mTopEdge.getEdgeRate() : mTopEdge.getFlingRate(vOffset);
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (mTopEdge.isEdgeOver() || -ry <= mTopEdge.getTargetOffset() - vOffset) {
                vOffset -= ry;
                consumed[1] += dy;
                dy = 0;
            } else {
                int yConsumed = (int) ((vOffset - mTopEdge.getTargetOffset()) / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = mTopEdge.getTargetOffset();
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeBottomScrollDown(int dy, int[] consumed, int type) {
        if (dy > 0 && mBottomEdge != null && isEdgeEnabled(Direction.BOTTOM) && !mTargetView.canScrollVertically(1) &&
                (type == ViewCompat.TYPE_TOUCH || mBottomEdge.isFlingFromTarget())) {
            int vOffset = mOffsetHelper.getTopAndBottomOffset();
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mBottomEdge.getEdgeRate() : mBottomEdge.getFlingRate(-vOffset);
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (mBottomEdge.isEdgeOver() || vOffset - ry >= -mBottomEdge.getTargetOffset()) {
                vOffset -= ry;
                consumed[1] += dy;
                dy = 0;
            } else {
                int yConsumed = (int) ((-mBottomEdge.getTargetOffset() - vOffset) / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = -mBottomEdge.getTargetOffset();
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeBottomScrollUp(int dy, int[] consumed, int type) {
        int vOffset = mOffsetHelper.getTopAndBottomOffset();
        if (dy < 0 && mBottomEdge != null && isEdgeEnabled(Direction.BOTTOM) && vOffset < 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mBottomEdge.getEdgeRate() : 1f;
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (vOffset <= ry) {
                consumed[1] += dy;
                vOffset -= ry;
                dy = 0;
            } else {
                int yConsumed = (int) (vOffset / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = 0;
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeLeftScrollRight(int dx, int[] consumed, int type) {
        int hOffset = mOffsetHelper.getLeftAndRightOffset();
        if (dx > 0 && mLeftEdge != null && isEdgeEnabled(Direction.LEFT) && hOffset > 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mLeftEdge.getEdgeRate() : 1f;
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (hOffset >= rx) {
                consumed[0] += dx;
                hOffset -= rx;
                dx = 0;
            } else {
                int xConsumed = (int) (hOffset / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = 0;
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }

    private int checkEdgeLeftScrollLeft(int dx, int[] consumed, int type) {
        int hOffset = mOffsetHelper.getLeftAndRightOffset();
        if (dx < 0 && mLeftEdge != null && isEdgeEnabled(Direction.LEFT) && !mTargetView.canScrollHorizontally(-1) &&
                (type == ViewCompat.TYPE_TOUCH || mLeftEdge.isFlingFromTarget())) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mLeftEdge.getEdgeRate() : mLeftEdge.getFlingRate(hOffset);
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (mLeftEdge.isEdgeOver() || -rx <= mLeftEdge.getTargetOffset() - hOffset) {
                hOffset -= rx;
                consumed[0] += dx;
                dx = 0;
            } else {
                int xConsumed = (int) ((hOffset - mLeftEdge.getTargetOffset()) / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = mLeftEdge.getTargetOffset();
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }

    private int checkEdgeRightScrollRight(int dx, int[] consumed, int type) {
        if (dx > 0 && mRightEdge != null && isEdgeEnabled(Direction.RIGHT) && !mTargetView.canScrollHorizontally(1) &&
                (type == ViewCompat.TYPE_TOUCH || mRightEdge.isFlingFromTarget())) {
            int hOffset = mOffsetHelper.getLeftAndRightOffset();
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mRightEdge.getEdgeRate() : mRightEdge.getFlingRate(-hOffset);
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (mRightEdge.isEdgeOver() || hOffset - rx >= -mRightEdge.getTargetOffset()) {
                hOffset -= rx;
                consumed[0] += dx;
                dx = 0;
            } else {
                int xConsumed = (int) ((-mRightEdge.getTargetOffset() - hOffset) / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = -mRightEdge.getTargetOffset();
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }

    private int checkEdgeRightScrollLeft(int dx, int[] consumed, int type) {
        int hOffset = mOffsetHelper.getLeftAndRightOffset();
        if (dx < 0 && mRightEdge != null && isEdgeEnabled(Direction.RIGHT) && hOffset < 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mRightEdge.getEdgeRate() : 1f;
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (hOffset <= dx) {
                consumed[0] += dx;
                hOffset -= rx;
                dx = 0;
            } else {
                int xConsumed = (int) (hOffset / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = 0;
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }


    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean mTarget = false;
        @Direction
        public int mDirection = Direction.TOP;
        public int mEdgeStartOffset = 0;
        public int mEdgeTargetOffset = ViewGroup.LayoutParams.WRAP_CONTENT;
        public float mEdgeRate = Edge.EDGE_RATE_DEFAULT;
        public float mEdgeScrollFling = Constants.SCROLL_FLING_DEFAULT;
        public float mEdgeScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;
        public boolean mEdgeOver = true;
        public boolean mFlingFromTarget = true;
        public boolean mEdgeScrollOffset = false;
        public boolean mEdgeScrollTouchUp = true;


        public LayoutParams(@NonNull Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout_Layout);
            mTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeIsTarget, mTarget);
            if (!mTarget) {
                mDirection = array.getInteger(R.styleable.EdgeLayout_Layout_direction, Direction.TOP);
                mEdgeStartOffset = array.getDimensionPixelSize(R.styleable.EdgeLayout_Layout_edgeStartOffset, mEdgeStartOffset);
                mEdgeTargetOffset = array.getLayoutDimension(R.styleable.EdgeLayout_Layout_edgeTargetOffset, Constants.INVALID);
                mEdgeOver = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeOver, mEdgeOver);
                mEdgeRate = array.getFloat(R.styleable.EdgeLayout_Layout_edgeRate, mEdgeRate);
                mEdgeScrollFling = array.getFloat(R.styleable.EdgeLayout_Layout_edgeFlingFraction, mEdgeScrollFling);
                mEdgeScrollSpeed = array.getFloat(R.styleable.EdgeLayout_Layout_edgeScrollSpeed, mEdgeScrollSpeed);
                mFlingFromTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeFlingFromTarget, mFlingFromTarget);
                mEdgeScrollOffset = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollOffset, mEdgeScrollOffset);
                mEdgeScrollTouchUp = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollTouchUp, mEdgeScrollTouchUp);
            }
            array.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }

    private static class DefaultStopTargetViewFlingImpl implements StopTargetViewFling {

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
            if (view instanceof ListView) ((ListView) view).onStopNestedScroll(view);

        }
    }

}
