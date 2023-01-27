package com.colin.library.android.widgets.edge;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RestrictTo;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityRecordCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.annotation.Orientation;
import com.colin.library.android.widgets.annotation.ScrollState;

import java.util.Locale;


public class EdgeLayout extends FrameLayout implements NestedScrollingParent3 {


    ///////////////////////////////////////////////////////////////////////////
    // 对外公开接口
    ///////////////////////////////////////////////////////////////////////////


    public static final Interpolator INTERPOLATOR = new DecelerateInterpolator(2.0F);

    private View mTargetView;
    private ViewOffsetHelper mTargetViewOffsetHelper;
    private float mNestedPreFlingVelocityScaleDown = 10;
    private int mScrollState = ScrollState.SCROLL_STATE_IDLE;

    private final OverScroller mScroller;
    private int mLastScrollerX;
    private int mLastScrollerY;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final EdgeHelper mEdgeHelper;
    private boolean mNestScrollEdge;

    public EdgeLayout(@NonNull Context context) {
        this(context, null, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        setChildrenDrawingOrderEnabled(true);
        mEdgeHelper = new EdgeHelper(this);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout, defStyleAttr, 0);
        mEdgeHelper.setDirectionEnabled(array.getInt(R.styleable.EdgeLayout_direction, Direction.TOP | Direction.BOTTOM));
        array.recycle();
        mScroller = new OverScroller(context, INTERPOLATOR);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int count = getChildCount();
        if (count == 0) return;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.mTarget) setTargetView(child);
            else setEdgeView(child, params);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        mEdgeHelper.onLayout(width, height);
        if (mTargetView != null) {
            final int childLeft = getPaddingLeft();
            final int childTop = getPaddingTop();
            final int childWidth = width - getPaddingLeft() - getPaddingRight();
            final int childHeight = height - getPaddingTop() - getPaddingBottom();
            mTargetView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        final int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mEdgeHelper.onMeasure(width, height);
        if (mTargetView != null) {
            final int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            final int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            mTargetView.measure(widthSpec, heightSpec);
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


    ///////////////////////////////////////////////////////////////////////////
    // Scrolling parent start
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        LogUtil.log("onNestedScrollAccepted:axes%d type:%d ", axes, type);
        getNestedScrollingParentHelper().onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(axes, type);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH);
    }


    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        LogUtil.log("onStartNestedScroll:axes%d type:%d ", axes, type);
        return mTargetView == target && (axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && mEdgeHelper.isHorizontalEnabled()) || (axes == ViewCompat.SCROLL_AXIS_VERTICAL && mEdgeHelper.isVerticalEnabled());
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        LogUtil.log("onNestedPreScroll:dx%d dy:%d consumed:%s type:%d", dx, dy, StringUtil.toString(consumed), type);
        // TODO: 2023/1/27
        dispatchNestedPreScroll(dx, dy, consumed, null, type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH, null);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, null);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @Nullable int[] consumed) {
        LogUtil.log("onNestedScroll:dxConsumed%d dyConsumed:%d  dxUnconsumed:%d  dyUnconsumed:%d type:%d consumed:%s", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type,
                StringUtil.toString(consumed));
        onNestedScrollInternal(dyUnconsumed, type, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        LogUtil.log("onStopNestedScroll");
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        LogUtil.log("onStopNestedScroll: type:%d ", type);
        getNestedScrollingParentHelper().onStopNestedScroll(target, type);
        stopNestedScroll(type);
    }


    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        LogUtil.log("onNestedFling:velocityX:%.2f velocityY:%.2f consumed:%s", velocityX, velocityY, String.valueOf(consumed));
        if (!consumed) {
            dispatchNestedFling(0, velocityY, true);
            fling((int) velocityY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        LogUtil.log("onNestedPreFling:velocityX:%.2f velocityY:%.2f ", velocityX, velocityY);
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        final int axes = getNestedScrollingParentHelper().getNestedScrollAxes();
        LogUtil.log("getNestedScrollAxes:axes%d", axes);
        return axes;
    }

    private void onNestedScrollInternal(int dyUnconsumed, int type, @Nullable int[] consumed) {
        LogUtil.log("onNestedScrollInternal:dyUnconsumed%d type:%d consumed:%s", dyUnconsumed, type, StringUtil.toString(consumed));
        final int oldScrollY = getScrollY();
        scrollBy(0, dyUnconsumed);
        final int myConsumed = getScrollY() - oldScrollY;
        if (consumed != null) consumed[1] += myConsumed;
        final int myUnconsumed = dyUnconsumed - myConsumed;
        getScrollingChildHelper().dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type, consumed);
    }
    ///////////////////////////////////////////////////////////////////////////
    // Scrolling parent end
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Scrolling child start
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        LogUtil.log("startNestedScroll:axes%d type%d", axes, type);
        mEdgeHelper.startNestedScroll(axes, type);
        return getScrollingChildHelper().startNestedScroll(axes, type);

    }

    @Override
    public void stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll(int type) {
        LogUtil.log("stopNestedScroll:type%d", type);
        mEdgeHelper.stopNestedScroll(type);
        getScrollingChildHelper().stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        LogUtil.log("hasNestedScrollingParent:type%d", type);
        return getScrollingChildHelper().hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        LogUtil.log("dispatchNestedPreScroll:dx%d dy:%d consumed:%s offsetInWindow:%s type:%d", dx, dy, StringUtil.toString(consumed), StringUtil.toString(offsetInWindow), type);
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        LogUtil.log("dispatchNestedScroll:dxConsumed%d dyConsumed:%d dxUnconsumed%d dyUnconsumed:%d  offsetInWindow:%s type:%d", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                StringUtil.toString(offsetInWindow), type);
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type, @NonNull int[] consumed) {
        LogUtil.log("dispatchNestedScroll:dxConsumed%d dyConsumed:%d dxUnconsumed%d dyUnconsumed:%d  offsetInWindow:%s type:%d consumed:%s", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                StringUtil.toString(offsetInWindow), type, StringUtil.toString(consumed));
        getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        LogUtil.log("dispatchNestedPreFling:velocityX:%.2f velocityX:%.2f ", velocityX, velocityY);
        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        LogUtil.log("dispatchNestedFling:velocityX:%.2f velocityX:%.2f consumed%s", velocityX, velocityY, String.valueOf(consumed));
        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Scrolling child end
    ///////////////////////////////////////////////////////////////////////////


    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Override
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Override
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Override
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Override
    public int computeVerticalScrollRange() {
        final int count = getChildCount();
        final int parentSpace = getHeight() - getPaddingBottom() - getPaddingTop();
        if (count == 0) return parentSpace;
        final View child = getChildAt(0);
        NestedScrollView.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        int scrollRange = child.getBottom() + lp.bottomMargin;
        final int scrollY = getScrollY();
        final int overscrollBottom = Math.max(0, scrollRange - parentSpace);
        if (scrollY < 0) {
            scrollRange -= scrollY;
        } else if (scrollY > overscrollBottom) {
            scrollRange += scrollY - overscrollBottom;
        }
        LogUtil.log("computeVerticalScrollRange:offset:%d ", scrollRange);
        return scrollRange;
    }

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Override
    public int computeVerticalScrollOffset() {
        final int offset = Math.max(0, super.computeVerticalScrollOffset());
        LogUtil.log("computeVerticalScrollOffset:offset:%d ", offset);
        return offset;
    }

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Override
    public int computeVerticalScrollExtent() {
        return 0;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Scrolling child start
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void computeScroll() {
        if (mScroller.isFinished()) return;
        mScroller.computeScrollOffset();
        final int currentX = mScroller.getCurrX();
        final int currentY = mScroller.getCurrY();
        int unconsumedX = currentX - mLastScrollerX;
        int unconsumedY = currentY - mLastScrollerY;
        mLastScrollerX = currentX;
        mLastScrollerY = currentY;
        mScrollConsumed[0] = 0;
        mScrollConsumed[1] = 0;
        // Nested Scrolling Pre Pass
        dispatchNestedPreScroll(unconsumedX, unconsumedY, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH);

        mLastScrollerX -= mScrollConsumed[0];
        mLastScrollerY -= mScrollConsumed[1];


        if (!mScroller.computeScrollOffset()) return;
        if (!mScroller.isFinished()) {
            final int currentX = mScroller.getCurrX();
            final int currentY = mScroller.getCurrY();
            mTargetViewOffsetHelper.setOffset(currentX, currentY);
            mEdgeHelper.scroll(currentX, currentY);
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }


        if (mState == STATE_TRIGGERING) return;

        if (mState == STATE_SETTLING_TO_INIT_OFFSET) {
            mState = ScrollState.SCROLL_STATE_IDLE;
            return;
        }
        if (mState == STATE_SETTLING_FLING) {
            checkScroll();
            return;
        }
        if (mState == STATE_SETTLING_TO_TRIGGER_OFFSET) {
            final int finalX = mScroller.getFinalX();
            final int finalY = mScroller.getFinalY();
            edgeStart(getDirectionEdge(Direction.LEFT), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.TOP), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.RIGHT), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.BOTTOM), finalX, finalY);
            edgeUpdateOffset(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }


    public void fling(int velocityY) {
        if (getChildCount() > 0) {

            mScroller.fling(getScrollX(), getScrollY(), // start
                    0, velocityY, // velocities
                    0, 0, // x
                    Integer.MIN_VALUE, Integer.MAX_VALUE, // y
                    0, 0); // overscroll
            runAnimatedScroll(true);
        }
    }

    private void runAnimatedScroll(boolean participateInNestedScrolling) {
        if (participateInNestedScrolling) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        } else {
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
        }
        mLastScrollerY = getScrollY();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void abortAnimatedScroll() {
        mScroller.abortAnimation();
        stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
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
        final Edge.Builder builder = new Edge.Builder(view, params.mDirection).setEdgeOver(params.mEdgeOver).setEdgeRate(params.mEdgeRate).setScrollTouchUp(params.mScrollTouchUp).setFlingFromTarget(
                params.mFlingFromTarget).setScrollFling(params.mScrollFling).setOffsetInit(params.mOffsetInit).setOffsetTarget(params.mOffsetTarget).setScrollOffset(
                params.mScrollOffset).setScrollSpeed(params.mScrollSpeed);
        view.setLayoutParams(params);
        setEdgeView(builder);
    }

    public void setEdgeView(@NonNull Edge.Builder builder) {
        if (builder.getView().getParent() != this) {
            throw new RuntimeException("Edge already exists other parent view.");
        }
        if (builder.getView().getParent() == null) {
            ViewGroup.LayoutParams lp = builder.getView().getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            addView(builder.getView(), lp);
        }
        mEdgeHelper.build(builder.getDirection(), builder);
    }

    public void setDirectionEnabled(@Direction int direction) {
        mEdgeHelper.setDirectionEnabled(direction);
    }


    public void setNestedPreFlingVelocityScaleDown(float nestedPreFlingVelocityScaleDown) {
        this.mNestedPreFlingVelocityScaleDown = nestedPreFlingVelocityScaleDown;
    }


    public void setEdgeListener(@Nullable OnEdgeListener edgeListener) {
        mEdgeHelper.setEdgeListener(edgeListener);
    }


    public void setEdgeScrollDuration(long duration) {
        mEdgeHelper.setScrollDuration(duration);
    }


    public void edgeStart(@Direction int direction) {
        mEdgeHelper.start(direction);
    }

    public void edgeFinish() {
        mEdgeHelper.finish();
    }

    public void edgeFinish(@Direction int direction) {
        mEdgeHelper.finish(direction);
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
        public int mOffsetInit;
        public int mOffsetTarget = ViewGroup.LayoutParams.WRAP_CONTENT;
        public float mEdgeRate = Edge.EDGE_RATE_DEFAULT;
        public float mScrollFling = Constants.SCROLL_FLING_DEFAULT;
        public float mScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;


        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout_Layout);
            mTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_target, mTarget);
            if (!mTarget) {
                mDirection = array.getInteger(R.styleable.EdgeLayout_Layout_direction, Direction.TOP);
                mOffsetInit = array.getDimensionPixelSize(R.styleable.EdgeLayout_Layout_edgeOffsetInit, mOffsetInit);
                mOffsetTarget = array.getLayoutDimension(R.styleable.EdgeLayout_Layout_edgeOffsetTarget, mOffsetTarget);
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


    ///////////////////////////////////////////////////////////////////////////
    // 辅助方法
    ///////////////////////////////////////////////////////////////////////////


    private void edgeUpdateOffset(@Nullable final Edge edge, final int offset) {
        if (edge != null) edge.updateOffset(offset);
    }

    private void scrollToTargetOffset(@NonNull Edge edge, @Orientation int orientation, int offset) {
        mTargetViewOffsetHelper.setOrientationOffset(orientation, offset);
        edge.scrollToTarget(offset);
    }


    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (mNestedScrollingChildHelper == null) {
            mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return mNestedScrollingChildHelper;
    }

    private NestedScrollingParentHelper getNestedScrollingParentHelper() {
        if (mNestedScrollingParentHelper == null) {
            mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }
        return mNestedScrollingParentHelper;
    }

    protected boolean isScrollVertical(int x, int y) {
        return Math.abs(y) >= Math.abs(x);
    }
}
