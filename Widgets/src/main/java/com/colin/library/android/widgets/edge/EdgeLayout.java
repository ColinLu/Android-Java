//package com.colin.library.android.widgets.edge;
//
//import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//import android.view.animation.Interpolator;
//import android.widget.FrameLayout;
//import android.widget.ListView;
//import android.widget.OverScroller;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RestrictTo;
//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.core.view.NestedScrollingChild3;
//import androidx.core.view.NestedScrollingChildHelper;
//import androidx.core.view.NestedScrollingParent3;
//import androidx.core.view.NestedScrollingParentHelper;
//import androidx.core.view.ViewCompat;
//import androidx.core.widget.ListViewCompat;
//import androidx.core.widget.NestedScrollView;
//
//import com.colin.library.android.utils.LogUtil;
//import com.colin.library.android.utils.StringUtil;
//import com.colin.library.android.widgets.R;
//import com.colin.library.android.widgets.annotation.Direction;
//import com.colin.library.android.widgets.annotation.Orientation;
//import com.colin.library.android.widgets.annotation.ScrollState;
//import com.colin.library.android.widgets.behavior.ViewOffsetHelper;
//import com.colin.library.android.widgets.def.Constants;
//import com.colin.library.android.widgets.interpolator.Interpolators;
//
///**
// * 作者： ColinLu
// * 时间： 2023-02-01 21:34
// * <p>
// * 描述： https://blog.csdn.net/jb_home/article/details/113757780
// * 在NestedScrollingChild滚动过程中，它和NestedScrollingParent会一直"保持通讯"，比如：
// * 当Child滚动之前，会通知Parent:"我要开始滚动啦，你看你要不要做点什么"。
// * 当Child在滚动的时候，也会每次通知Parent:"我这次消费了xxx，你看你还要做什么"。
// * 当Child滚动完成，Parent也会收到通知："我滚动完成了"。
// * 当然了，除了手指触摸滚动的，还有惯性滚动，但原理和流程是一样的
// * 因为child是产生滑动的造势者，所以它的api都是以直接的动词开头，而parent的滑动响应是child通知parent的，所以都是以监听on开头：
// * parent ----> onXXXX()
// * child -----> verbXXXX()
// */
//public class EdgeLayout extends CoordinatorLayout implements NestedScrollingParent3, NestedScrollingChild3 {
//
//    public static final Interpolator INTERPOLATOR = Interpolators.QUNITIC;
//    private final EdgeHelper mEdgeHelper;
//    private View mTargetView;
//    private ViewOffsetHelper mTargetViewOffsetHelper;
//    private final OverScroller mScroller;
//    private float mNestedPreFlingVelocityScaleDown = 10;
//    private int mScrollState = ScrollState.SCROLL_STATE_IDLE;
//    private int mLastScrollerY;
//    private float mInterceptDownY;
//    private NestedScrollingParentHelper mNestedScrollingParentHelper;
//    private NestedScrollingChildHelper mNestedScrollingChildHelper;
//    private final int[] mParentScrollConsumed = new int[2];
//    private final int[] mNestedScrollConsumed = new int[2];
//    private final int[] mParentOffsetInWindow = new int[2];
//    private final int mTouchSlop;
//    private int mTotalUnconsumedY;
//    private int mTotalDragDistanceY;
//    private boolean mScrollEdgeFling;//mReturningToStart
//    private boolean mNestScrollEdge;
//    private boolean mActionDragged;
//    private int mActivePointerId = Constants.INVALID;
//    private boolean mEnableLegacyRequestDisallowInterceptTouch;
//
//    public EdgeLayout(@NonNull Context context) {
//        this(context, null, R.attr.EdgeLayoutStyle);
//    }
//
//    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, R.attr.EdgeLayoutStyle);
//    }
//
//    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        setWillNotDraw(false);
//        setChildrenDrawingOrderEnabled(true);
//        setNestedScrollingEnabled(true);
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        mEdgeHelper = new EdgeHelper(this, attrs);
//        mScroller = new OverScroller(context, INTERPOLATOR);
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        final int count = getChildCount();
//        if (count == 0) return;
//        for (int i = 0; i < count; i++) {
//            final View child = getChildAt(i);
//            final LayoutParams params = (LayoutParams) child.getLayoutParams();
//            if (params.mTarget) setTargetView(child);
//            else setEdgeView(child, params);
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        final int width = getMeasuredWidth();
//        final int height = getMeasuredHeight();
//        if (mTargetView != null) {
//            final int childLeft = getPaddingLeft();
//            final int childTop = getPaddingTop();
//            final int childWidth = width - getPaddingLeft() - getPaddingRight();
//            final int childHeight = height - getPaddingTop() - getPaddingBottom();
//            mTargetView.layout(childLeft, 0, childLeft + childWidth, height);
//        }
//        mEdgeHelper.onLayout(width, height);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        final int action = ev.getActionMasked();
//        int pointerIndex = -1;
//        float pointerX, pointerY;
//        // Fail fast if we're not in a state where a swipe is possible
//        if (!isEnabled() || mNestScrollEdge || targetCanScroll()) {
//            return false;
//        }
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mActivePointerId = ev.getPointerId(0);
//                mActionDragged = false;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) return false;
//                pointerX = ev.getX(pointerIndex);
//                pointerY = ev.getY(pointerIndex);
//                mActionDragged = startDragging(pointerX, pointerY);
//                if (mActionDragged) {
//                    // While the spinner is being dragged down, our parent shouldn't try
//                    // to intercept touch events. It will stop the drag gesture abruptly.
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                    startScrollEdge(getNestedScrollAxes(), pointerX, pointerY);
//                } else return false;
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                pointerIndex = ev.getActionIndex();
//                if (pointerIndex < 0) return false;
//                mActivePointerId = ev.getPointerId(pointerIndex);
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                break;
//            case MotionEvent.ACTION_UP: {
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) return false;
//                if (mActionDragged) {
//                    mActionDragged = false;
//                    finishScrollEdge(getNestedScrollAxes(), ev.getX(pointerIndex), ev.getY(pointerIndex));
//                }
//                mActivePointerId = Constants.INVALID;
//                return false;
//            }
//            case MotionEvent.ACTION_CANCEL:
//                return false;
//        }
//
//        return true;
//    }
//
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (!isEnabled() || mEdgeHelper.isRunning() || targetCanScroll()) {
//            return true;
//        }
//        int pointerIndex;
//        switch (ev.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                mEdgeHelper.actionStart(getNestedScrollAxes());
//                mActivePointerId = ev.getPointerId(0);
//                mActionDragged = false;
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) return false;
//                mInterceptDownY = ev.getY(pointerIndex);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (mActivePointerId == Constants.INVALID) return false;
//                pointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (pointerIndex < 0) return false;
//                float x = ev.getX(pointerIndex);
//                float y = ev.getX(pointerIndex);
//                mActionDragged = startDragging(x, y);
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                mActionDragged = false;
//                mActivePointerId = Constants.INVALID;
//                break;
//            default:
//                break;
//        }
//        return mActionDragged;
//    }
//
//    private boolean startDragging(float x, float y) {
//        if (mActionDragged) return true;
//
//        final float distanceY = y - mInterceptDownY;
//        if (distanceY > mTouchSlop) return true;
//        return false;
//
//    }
//
//    private void onSecondaryPointerUp(MotionEvent ev) {
//        final int pointerIndex = ev.getActionIndex();
//        final int pointerId = ev.getPointerId(pointerIndex);
//        if (pointerId == mActivePointerId) {
//            // This was our active pointer going up. Choose a new
//            // active pointer and adjust accordingly.
//            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//            mActivePointerId = ev.getPointerId(newPointerIndex);
//        }
//    }
//
//    @Override
//    public void requestDisallowInterceptTouchEvent(boolean b) {
//        // if this is a List < L or another view that doesn't support nested
//        // scrolling, ignore this request so that the vertical scroll event
//        // isn't stolen
//        if (mTargetView != null && !ViewCompat.isNestedScrollingEnabled(mTargetView)) {
//            if (mEnableLegacyRequestDisallowInterceptTouch) {
//                // Nope.
//            } else {
//                // Ignore here, but pass it up to our parent
//                ViewParent parent = getParent();
//                if (parent != null) {
//                    parent.requestDisallowInterceptTouchEvent(b);
//                }
//            }
//        } else {
//            super.requestDisallowInterceptTouchEvent(b);
//        }
//    }
//
//    @Override
//    protected FrameLayout.LayoutParams generateLayoutParams(@NonNull ViewGroup.LayoutParams lp) {
//        return new LayoutParams(lp);
//    }
//
//    @Override
//    public FrameLayout.LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
//        return new LayoutParams(getContext(), attrs);
//    }
//
//    @Override
//    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
//        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }
//
//    @Override
//    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
//        return params instanceof LayoutParams;
//    }
//
//    ///////////////////////////////////////////////////////////////////////////
//    // Scrolling child start
//    ///////////////////////////////////////////////////////////////////////////
//    @Override
//    public void setNestedScrollingEnabled(boolean enabled) {
//        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
//    }
//
//    @Override
//    public boolean isNestedScrollingEnabled() {
//        return getScrollingChildHelper().isNestedScrollingEnabled();
//    }
//
//    /*action->move*/
//    @Override
//    public boolean startNestedScroll(int axes) {
//        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
//    }
//
//    /*action->move*/
//    @Override
//    public boolean startNestedScroll(int axes, int type) {
//        LogUtil.log("startNestedScroll:axes%d type:%d", axes, type);
//        return getScrollingChildHelper().startNestedScroll(axes, type);
//
//    }
//
//    @Override
//    public void stopNestedScroll() {
//        stopNestedScroll(ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public void stopNestedScroll(int type) {
//        LogUtil.log("stopNestedScroll:type%d", type);
//        getScrollingChildHelper().stopNestedScroll(type);
//    }
//
//    @Override
//    public boolean hasNestedScrollingParent() {
//        return getScrollingChildHelper().hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public boolean hasNestedScrollingParent(int type) {
//        LogUtil.log("hasNestedScrollingParent:type%d", type);
//        return getScrollingChildHelper().hasNestedScrollingParent(type);
//    }
//
//    /*action->move*/
//    @Override
//    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
//        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
//    }
//
//    /*action->move*/
//    @Override
//    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
//        LogUtil.log("dispatchNestedPreScroll:dx:%d dy:%d consumed:%s offsetInWindow:%s type:%d", dx, dy, StringUtil.toString(consumed),
//                StringUtil.toString(offsetInWindow), type);
//        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
//    }
//
//    @Override
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
//        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
//        LogUtil.log("dispatchNestedScroll:dxConsumed:%d dyConsumed:%d dxUnconsumed:%d dyUnconsumed:%d  offsetInWindow:%s type:%d", dxConsumed,
//                dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(offsetInWindow), type);
//        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
//    }
//
//    @Override
//    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type,
//            @NonNull int[] consumed) {
//        LogUtil.log("dispatchNestedScroll:dxConsumed%d dyConsumed:%d dxUnconsumed%d dyUnconsumed:%d  offsetInWindow:%s type:%d consumed:%s",
//                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(offsetInWindow), type, StringUtil.toString(consumed));
//        getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
//    }
//
//    @Override
//    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
//        LogUtil.log("dispatchNestedPreFling:velocityX:%.2f velocityX:%.2f ", velocityX, velocityY);
//        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
//    }
//
//    @Override
//    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        LogUtil.log("dispatchNestedFling:velocityX:%.2f velocityX:%.2f consumed%s", velocityX, velocityY, String.valueOf(consumed));
//        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
//    }
//
//
//    ///////////////////////////////////////////////////////////////////////////
//    // Scrolling child end
//    ///////////////////////////////////////////////////////////////////////////
//
//    ///////////////////////////////////////////////////////////////////////////
//    // Scrolling parent start
//    ///////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
//        return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
//        LogUtil.log("onStartNestedScroll:axes:%d type:%d ", axes, type);
//        return isEnabled() && mTargetView == target && mEdgeHelper.isNestedScrollEnabled(axes);
//    }
//
//    @Override
//    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
//        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
//        LogUtil.log("onNestedScrollAccepted:axes:%d type:%d ", axes, type);
//        getNestedScrollingParentHelper().onNestedScrollAccepted(child, target, axes, type);
////        startNestedScroll(axes, type);
//        mTotalUnconsumedY = 0;
////        mNestedScrollInProgress = true;
//    }
//
//
//    @Override
//    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
//        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
//        LogUtil.log("onNestedPreScroll:dx:%d dy:%d consumed:%s type:%d", dx, dy, StringUtil.toString(consumed), type);
//        if (dy > 0 && mTotalUnconsumedY > 0) {
//            if (dy > mTotalUnconsumedY) {
//                consumed[1] = mTotalUnconsumedY;
//                mTotalUnconsumedY = 0;
//            } else {
//                mTotalUnconsumedY -= dy;
//                consumed[1] = dy;
//            }
//            startScrollVertically(mTotalUnconsumedY);
//        }
//
////        if (mUsingCustomStart && dy > 0 && mTotalUnconsumedY == 0
////                && Math.abs(dy - consumed[1]) > 0) {
////           // edgeTop setVisibility(View.GONE);
////        }
//        final int[] parentConsumed = mParentScrollConsumed;
//        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, mParentOffsetInWindow)) {
//            consumed[0] += parentConsumed[0];
//            consumed[1] += parentConsumed[1];
//        }
//        LogUtil.log("onNestedPreScroll:dx:%d dy:%d consumed:%s type:%d", dx, dy, StringUtil.toString(consumed), type);
//    }
//
//
//    @Override
//    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH, mNestedScrollConsumed);
//    }
//
//    @Override
//    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
//        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, mNestedScrollConsumed);
//    }
//
//    @Override
//    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type,
//            @NonNull int[] consumed) {
//        LogUtil.log("onNestedScroll:dxConsumed:%d dyConsumed:%d  dxUnconsumed:%d  dyUnconsumed:%d type:%d consumed:%s", dxConsumed, dyConsumed,
//                dxUnconsumed, dyUnconsumed, type, StringUtil.toString(consumed));
//        if (type != ViewCompat.TYPE_TOUCH) return;
//        final int consumedXBeforeParents = consumed[0];
//        final int consumedYBeforeParents = consumed[1];
//        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow, type, consumed);
//
//        final int consumedXByParents = consumed[0] - consumedXBeforeParents;
//        final int consumedYByParents = consumed[1] - consumedYBeforeParents;
//
//        final int unconsumedXAfterParents = dxUnconsumed - consumedXByParents;
//        final int unconsumedYAfterParents = dyUnconsumed - consumedYByParents;
//
//        final int remainingDistanceToScrollX = unconsumedXAfterParents == 0 ? dxUnconsumed + mParentOffsetInWindow[0] : unconsumedXAfterParents;
//        final int remainingDistanceToScrollY = unconsumedYAfterParents == 0 ? dyUnconsumed + mParentOffsetInWindow[1] : unconsumedYAfterParents;
//
//        if (remainingDistanceToScrollX < 0 && !canChildScrollHorizontallyUp()) {
//            mTotalUnconsumedX += Math.abs(remainingDistanceToScrollX);
//            startScrollHorizontally(mTotalUnconsumedX);
//            // If we've gotten here, we need to consume whatever is left to consume, which at this
//            // point is either equal to 0, or remainingDistanceToScroll.
//            consumed[0] += unconsumedXAfterParents;
//        }
//
//        if (remainingDistanceToScrollY < 0 && !canChildScrollVerticallyUp()) {
//            mTotalUnconsumedY += Math.abs(remainingDistanceToScrollY);
//            startScrollVertically(mTotalUnconsumedY);
//            // If we've gotten here, we need to consume whatever is left to consume, which at this
//            // point is either equal to 0, or remainingDistanceToScroll.
//            consumed[1] += unconsumedYAfterParents;
//        }
//    }
//
//    private void startScrollEdge(@ViewCompat.ScrollAxis int axis, float pointerX, float pointerY) {
//        LogUtil.log("startScrollEdge:axis:%s,pointerX:%s pointerY:%s", axis, pointerX, pointerY);
//    }
//
//    private void finishScrollEdge(@ViewCompat.ScrollAxis int axis, float pointerX, float pointerY) {
//        LogUtil.log("finishScrollEdge:axis:%s,pointerX:%s pointerY:%s", axis, pointerX, pointerY);
//    }
//
//    private void startScrollVertically(float scrollTop) {
//        LogUtil.log(String.format("startScrollVertically:%s", scrollTop));
//    }
//
//    private void startScrollHorizontally(float scrollLeft) {
//        LogUtil.log(String.format("startScrollHorizontally:%s", scrollLeft));
////        mProgress.setArrowEnabled(true);
////        float originalDragPercent = overscrollTop / mTotalDragDistance;
////
////        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
////        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
////        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
////        float slingshotDist = mCustomSlingshotDistance > 0
////                ? mCustomSlingshotDistance
////                : (mUsingCustomStart
////                ? mSpinnerOffsetEnd - mOriginalOffsetTop
////                : mSpinnerOffsetEnd);
////        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
////                / slingshotDist);
////        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
////                (tensionSlingshotPercent / 4), 2)) * 2f;
////        float extraMove = slingshotDist * tensionPercent * 2;
////
////        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
////        // where 1.0f is a full circle
////        if (mCircleView.getVisibility() != View.VISIBLE) {
////            mCircleView.setVisibility(View.VISIBLE);
////        }
////        if (!mScale) {
////            mCircleView.setScaleX(1f);
////            mCircleView.setScaleY(1f);
////        }
////
////        if (mScale) {
////            setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
////        }
////        if (overscrollTop < mTotalDragDistance) {
////            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA
////                    && !isAnimationRunning(mAlphaStartAnimation)) {
////                // Animate the alpha
////                startProgressAlphaStartAnimation();
////            }
////        } else {
////            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
////                // Animate the alpha
////                startProgressAlphaMaxAnimation();
////            }
////        }
////        float strokeStart = adjustedPercent * .8f;
////        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
////        mProgress.setArrowScale(Math.min(1f, adjustedPercent));
////
////        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
////        mProgress.setProgressRotation(rotation);
////        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
//    }
//
//    private void finishScrollVertical(int scrollTop) {
//        LogUtil.log(String.format("finishScrollVertical:%s", scrollTop));
//        if (Math.abs(scrollTop) > Math.abs(mTotalDragDistanceY)) {
//            mEdgeHelper.start(scrollTop > 0 ? Direction.TOP : Direction.BOTTOM);
//        } else {
//            // cancel refresh
//            mEdgeHelper.finish(scrollTop > 0 ? Direction.TOP : Direction.BOTTOM);
//        }
//    }
//
//    private void finishScrollHorizontally(int scrollLeft) {
//        LogUtil.log(String.format("finishScrollHorizontally:%s", scrollLeft));
//        if (Math.abs(scrollLeft) > Math.abs(mTotalDragDistanceX)) {
//            mEdgeHelper.start(scrollLeft > 0 ? Direction.LEFT : Direction.RIGHT);
//        } else {
//            // cancel refresh
//            mEdgeHelper.finish(scrollLeft > 0 ? Direction.LEFT : Direction.RIGHT);
//        }
//    }
//
//
//    @Override
//    public void onStopNestedScroll(@NonNull View target) {
//        LogUtil.log("onStopNestedScroll");
//        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public void onStopNestedScroll(@NonNull View target, int type) {
//        LogUtil.log("onStopNestedScroll: type:%d ", type);
//        if (type != ViewCompat.TYPE_TOUCH) return;
//        getNestedScrollingParentHelper().onStopNestedScroll(target, type);
//        if (mTotalUnconsumedX > 0) {
//            finishScrollHorizontally(mTotalUnconsumedX);
//            mTotalUnconsumedX = 0;
//        }
//        if (mTotalUnconsumedY > 0) {
//            finishScrollVertical(mTotalUnconsumedY);
//            mTotalUnconsumedY = 0;
//        }
//        stopNestedScroll(type);
//    }
//
//
//    @Override
//    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
//        LogUtil.log("onNestedFling:velocityX:%.2f velocityY:%.2f consumed:%s", velocityX, velocityY, String.valueOf(consumed));
//        if (!consumed) {
//            dispatchNestedFling(0, velocityY, true);
//            fling((int) velocityY);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
//        LogUtil.log("onNestedPreFling:velocityX:%.2f velocityY:%.2f ", velocityX, velocityY);
//        return dispatchNestedPreFling(velocityX, velocityY);
//    }
//
//    @Override
//    public int getNestedScrollAxes() {
//        final int axes = getNestedScrollingParentHelper().getNestedScrollAxes();
//        LogUtil.log("getNestedScrollAxes:axes%d", axes);
//        return axes;
//    }
//
//    private void onNestedScrollInternal(int dyUnconsumed, int type, @Nullable int[] consumed) {
//        LogUtil.log("onNestedScrollInternal:dyUnconsumed:%d type:%d consumed:%s", dyUnconsumed, type, StringUtil.toString(consumed));
//        final int oldScrollY = getScrollY();
//        scrollBy(0, dyUnconsumed);
//        final int myConsumed = getScrollY() - oldScrollY;
//        if (consumed != null) consumed[1] += myConsumed;
//        final int myUnconsumed = dyUnconsumed - myConsumed;
//        getScrollingChildHelper().dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type, consumed);
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    // Scrolling parent end
//    ///////////////////////////////////////////////////////////////////////////
//
//
//    @RestrictTo(LIBRARY_GROUP_PREFIX)
//    @Override
//    public int computeHorizontalScrollRange() {
//        return super.computeHorizontalScrollRange();
//    }
//
//    @RestrictTo(LIBRARY_GROUP_PREFIX)
//    @Override
//    public int computeHorizontalScrollOffset() {
//        return super.computeHorizontalScrollOffset();
//    }
//
//    @RestrictTo(LIBRARY_GROUP_PREFIX)
//    @Override
//    public int computeHorizontalScrollExtent() {
//        return super.computeHorizontalScrollExtent();
//    }
//
//    @RestrictTo(LIBRARY_GROUP_PREFIX)
//    @Override
//    public int computeVerticalScrollRange() {
//        final int count = getChildCount();
//        final int parentSpace = getHeight() - getPaddingBottom() - getPaddingTop();
//        if (count == 0) return parentSpace;
//        final View child = getChildAt(0);
//        NestedScrollView.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
//        int scrollRange = child.getBottom() + lp.bottomMargin;
//        final int scrollY = getScrollY();
//        final int overscrollBottom = Math.max(0, scrollRange - parentSpace);
//        if (scrollY < 0) {
//            scrollRange -= scrollY;
//        } else if (scrollY > overscrollBottom) {
//            scrollRange += scrollY - overscrollBottom;
//        }
//        LogUtil.log("computeVerticalScrollRange:offset:%d ", scrollRange);
//        return scrollRange;
//    }
//
//    @RestrictTo(LIBRARY_GROUP_PREFIX)
//    @Override
//    public int computeVerticalScrollOffset() {
//        final int offset = Math.max(0, super.computeVerticalScrollOffset());
//        LogUtil.log("computeVerticalScrollOffset:offset:%d ", offset);
//        return offset;
//    }
//
//    @RestrictTo(LIBRARY_GROUP_PREFIX)
//    @Override
//    public int computeVerticalScrollExtent() {
//        return 0;
//    }
//
//
//    ///////////////////////////////////////////////////////////////////////////
//    // Scrolling child start
//    ///////////////////////////////////////////////////////////////////////////
////    @Override
////    public void computeScroll() {
////        if (mScroller.isFinished()) return;
////        mScroller.computeScrollOffset();
////        final int currentX = mScroller.getCurrX();
////        final int currentY = mScroller.getCurrY();
////        int unconsumedX = currentX - mLastScrollerX;
////        int unconsumedY = currentY - mLastScrollerY;
////        mLastScrollerX = currentX;
////        mLastScrollerY = currentY;
////        mScrollConsumed[0] = 0;
////        mScrollConsumed[1] = 0;
////        // Nested Scrolling Pre Pass
////        dispatchNestedPreScroll(unconsumedX, unconsumedY, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH);
////
////        mLastScrollerX -= mScrollConsumed[0];
////        mLastScrollerY -= mScrollConsumed[1];
////
////
////        if (!mScroller.computeScrollOffset()) return;
////        if (!mScroller.isFinished()) {
////            final int currentX = mScroller.getCurrX();
////            final int currentY = mScroller.getCurrY();
////            mTargetViewOffsetHelper.setOffset(currentX, currentY);
////            mEdgeHelper.scroll(currentX, currentY);
////            ViewCompat.postInvalidateOnAnimation(this);
////            return;
////        }
////
////
////        if (mState == STATE_TRIGGERING) return;
////
////        if (mState == STATE_SETTLING_TO_INIT_OFFSET) {
////            mState = ScrollState.SCROLL_STATE_IDLE;
////            return;
////        }
////        if (mState == STATE_SETTLING_FLING) {
////            checkScroll();
////            return;
////        }
////        if (mState == STATE_SETTLING_TO_TRIGGER_OFFSET) {
////            final int finalX = mScroller.getFinalX();
////            final int finalY = mScroller.getFinalY();
////            edgeStart(getDirectionEdge(Direction.LEFT), finalX, finalY);
////            edgeStart(getDirectionEdge(Direction.TOP), finalX, finalY);
////            edgeStart(getDirectionEdge(Direction.RIGHT), finalX, finalY);
////            edgeStart(getDirectionEdge(Direction.BOTTOM), finalX, finalY);
////            edgeUpdateOffset(mScroller.getCurrX(), mScroller.getCurrY());
////        }
////    }
//
//
//    public void fling(int velocityY) {
//        if (getChildCount() > 0) {
//
//            mScroller.fling(getScrollX(), getScrollY(), // start
//                    0, velocityY, // velocities
//                    0, 0, // x
//                    Integer.MIN_VALUE, Integer.MAX_VALUE, // y
//                    0, 0); // overscroll
//            runAnimatedScroll(true);
//        }
//    }
//
//    private void runAnimatedScroll(boolean participateInNestedScrolling) {
//        if (participateInNestedScrolling) {
//            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
//        } else {
//            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
//        }
//        mLastScrollerY = getScrollY();
//        ViewCompat.postInvalidateOnAnimation(this);
//    }
//
//    private void abortAnimatedScroll() {
//        mScroller.abortAnimation();
//        stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
//    }
//
//    public void setTargetView(@NonNull View view) {
//        if (view.getParent() != this) {
//            throw new RuntimeException("Target already exists other parent view.");
//        }
//        if (view.getParent() == null) {
//            addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }
//        mTargetView = view;
//        mTargetViewOffsetHelper = new ViewOffsetHelper(view);
//    }
//
//    public void setEdgeView(@NonNull View view, @NonNull LayoutParams params) {
//        final Edge.Builder builder = new Edge.Builder(view).setEdgeOver(params.mEdgeOver).setEdgeRate(params.mEdgeRate).setScrollTouchUp(
//                params.mScrollTouchUp).setFlingFromTarget(params.mFlingFromTarget).setScrollFling(params.mScrollFling).setOffsetInit(
//                params.mOffsetInit).setOffsetTarget(params.mOffsetTarget).setScrollOffset(params.mScrollOffset).setScrollSpeed(params.mScrollSpeed);
//        view.setLayoutParams(params);
//        setEdgeView(params.mDirection, builder);
//    }
//
//    public void setEdgeView(@Direction int direction, @NonNull Edge.Builder builder) {
//        if (builder.getView().getParent() != this) {
//            throw new RuntimeException("Edge already exists other parent view.");
//        }
//        if (builder.getView().getParent() == null) {
//            ViewGroup.LayoutParams lp = builder.getView().getLayoutParams();
//            if (lp == null) {
//                lp = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            }
//            addView(builder.getView(), lp);
//        }
//        mEdgeHelper.build(direction, builder);
//    }
//
//    public void setDirectionEnabled(@Direction int direction) {
//        mEdgeHelper.setDirectionEnabled(direction);
//    }
//
//
//    public void setNestedPreFlingVelocityScaleDown(float nestedPreFlingVelocityScaleDown) {
//        this.mNestedPreFlingVelocityScaleDown = nestedPreFlingVelocityScaleDown;
//    }
//
//
//    public void setEdgeListener(@Nullable OnEdgeListener edgeListener) {
//        mEdgeHelper.setEdgeListener(edgeListener);
//    }
//
//
//    public void setEdgeScrollDuration(long duration) {
//        mEdgeHelper.setScrollDuration(duration);
//    }
//
//
//    public void edgeStart(@Direction int direction) {
//        mEdgeHelper.start(direction);
//    }
//
//    public void edgeFinish() {
//        mEdgeHelper.finish();
//    }
//
//    public void edgeFinish(@Direction int direction) {
//        mEdgeHelper.finish(direction);
//    }
//
//    ///////////////////////////////////////////////////////////////////////////
//    // 内部类
//    ///////////////////////////////////////////////////////////////////////////
//    public static class LayoutParams extends FrameLayout.LayoutParams {
//        public boolean mTarget = false;
//        public boolean mEdgeOver;
//        public boolean mFlingFromTarget = true;
//        public boolean mScrollOffset = false;
//        public boolean mScrollTouchUp = true;
//        @Direction
//        public int mDirection;
//        public int mOffsetInit;
//        public int mOffsetTarget = ViewGroup.LayoutParams.WRAP_CONTENT;
//        public float mEdgeRate = Edge.EDGE_RATE_DEFAULT;
//        public float mScrollFling = Constants.SCROLL_FLING_DEFAULT;
//        public float mScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;
//
//
//        public LayoutParams(Context context, AttributeSet attrs) {
//            super(context, attrs);
//            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout_Layout);
//            mTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_target, mTarget);
//            if (!mTarget) {
//                mDirection = array.getInteger(R.styleable.EdgeLayout_Layout_direction, Direction.TOP);
//                mOffsetInit = array.getDimensionPixelSize(R.styleable.EdgeLayout_Layout_edgeOffsetInit, mOffsetInit);
//                mOffsetTarget = array.getLayoutDimension(R.styleable.EdgeLayout_Layout_edgeOffsetTarget, mOffsetTarget);
//                mEdgeOver = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeOver, mEdgeOver);
//                mEdgeRate = array.getFloat(R.styleable.EdgeLayout_Layout_edgeRate, mEdgeRate);
//                mScrollFling = array.getFloat(R.styleable.EdgeLayout_Layout_edgeFlingFraction, mScrollFling);
//                mScrollSpeed = array.getFloat(R.styleable.EdgeLayout_Layout_edgeScrollSpeed, mScrollSpeed);
//                mFlingFromTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeFlingFromTarget, mFlingFromTarget);
//                mScrollOffset = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollOffset, mScrollOffset);
//                mScrollTouchUp = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollTouchUp, mScrollTouchUp);
//            }
//            array.recycle();
//        }
//
//        public LayoutParams(int width, int height) {
//            super(width, height);
//        }
//
//        public LayoutParams(ViewGroup.LayoutParams params) {
//            super(params);
//        }
//
//        public LayoutParams(MarginLayoutParams params) {
//            super(params);
//        }
//    }
//
//
//    ///////////////////////////////////////////////////////////////////////////
//    // 辅助方法
//    ///////////////////////////////////////////////////////////////////////////
//
//
//    private void edgeUpdateOffset(@Nullable final Edge edge, final int offset) {
//        if (edge != null) edge.updateOffset(offset);
//    }
//
//    private void scrollToTargetOffset(@NonNull Edge edge, @Orientation int orientation, int offset) {
//        mTargetViewOffsetHelper.setOrientationOffset(orientation, offset);
//        edge.scrollToTarget(offset);
//    }
//
//
//    private NestedScrollingChildHelper getScrollingChildHelper() {
//        if (mNestedScrollingChildHelper == null) {
//            mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
//        }
//        return mNestedScrollingChildHelper;
//    }
//
//    private NestedScrollingParentHelper getNestedScrollingParentHelper() {
//        if (mNestedScrollingParentHelper == null) {
//            mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
//        }
//        return mNestedScrollingParentHelper;
//    }
//
//    protected boolean isScrollVertical(int x, int y) {
//        return Math.abs(y) >= Math.abs(x);
//    }
//
//    public boolean targetCanScroll() {
//        return targetCanScrollUp() || targetCanScrollBottom() || targetCanScrollLeft() || targetCanScrollRight();
//    }
//
//    public boolean targetCanScrollUp() {
//        if (mTargetView instanceof ListView) {
//            return ListViewCompat.canScrollList((ListView) mTargetView, -1);
//        }
//        return mTargetView.canScrollVertically(-1);
//    }
//
//    public boolean targetCanScrollBottom() {
//        if (mTargetView instanceof ListView) {
//            return ListViewCompat.canScrollList((ListView) mTargetView, 1);
//        }
//        return mTargetView.canScrollVertically(1);
//    }
//
//    public boolean targetCanScrollLeft() {
//        if (mTargetView instanceof ListView) return false;
//        return mTargetView.canScrollHorizontally(-1);
//    }
//
//    public boolean targetCanScrollRight() {
//        if (mTargetView instanceof ListView) return false;
//        return mTargetView.canScrollHorizontally(1);
//    }
//}
