package com.colin.library.android.widgets.scroll;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.colin.library.android.utils.NumberUtil;
import com.colin.library.android.widgets.Utils;
import com.colin.library.android.widgets.alpha.AlphaFrameLayout;
import com.colin.library.android.widgets.annotation.ScrollState;
import com.colin.library.android.widgets.behavior.ViewOffsetHelper;
import com.colin.library.android.widgets.interpolator.Interpolators;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 22:39
 * <p>
 * 描述： TODO
 */
public abstract class NestedScrollBottomFrameLayoutDelegate extends AlphaFrameLayout
        implements NestedScrollingChild2, NestedScrollingParent2, INestedScrollBottom {
    public static final String INSTANCE_STATE = "INSTANCE_STATE";
    public static final String INSTANCE_SCROLL_OFFSET = "INSTANCE_SCROLL_OFFSET";
    private static final int INVALID_POINTER = -1;

    private final NestedScrollingParentHelper mParentHelper;
    private final NestedScrollingChildHelper mChildHelper;
    private final ViewOffsetHelper mHeaderViewOffsetHelper;
    private final ViewOffsetHelper mContentViewOffsetHelper;
    private final View mHeaderView;
    private final View mContentView;
    private final Rect mTempRect = new Rect();
    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];
    private final Runnable mCheckLayoutAction = this::checkLayout;
    private final ViewFlinger mViewFlinger;

    private boolean isBeingDragged;
    private int activePointerId = INVALID_POINTER;
    private int lastMotionY;
    private int touchSlop = -1;
    private VelocityTracker velocityTracker;
    private int mNestedOffsetY = 0;
    private INestedScroll.OnScrollNotify mOnScrollNotifier;

    public NestedScrollBottomFrameLayoutDelegate(Context context) {
        this(context, null);
    }

    public NestedScrollBottomFrameLayoutDelegate(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public NestedScrollBottomFrameLayoutDelegate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);

        ViewCompat.setNestedScrollingEnabled(this, true);
        mHeaderView = onCreateHeaderView();
        mContentView = onCreateContentView();
        if (!(mContentView instanceof INestedScrollBottom)) {
            throw new IllegalStateException("the view create by onCreateContentView() should implement from INestedScrollBottom");
        }
        addView(mHeaderView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getHeaderHeightLayoutParam()));
        addView(mContentView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mHeaderViewOffsetHelper = new ViewOffsetHelper(mHeaderView);
        mContentViewOffsetHelper = new ViewOffsetHelper(mContentView);
        mViewFlinger = new ViewFlinger();
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getContentView() {
        return mContentView;
    }

    public int getOffsetCurrent() {
        return -mHeaderViewOffsetHelper.getTopAndBottomOffset();
    }

    public int getOffsetRange() {
        return -getMiniOffset();
    }

    private int getMiniOffset() {
        final INestedScrollBottom b = (INestedScrollBottom) mContentView;
        final int contentHeight = b.getContentHeight();
        final FrameLayout.LayoutParams headerLp = (LayoutParams) mHeaderView.getLayoutParams();
        int minOffset = -mHeaderView.getHeight() - headerLp.bottomMargin + getHeaderStickyHeight();
        if (contentHeight != INestedScrollBottom.HEIGHT_IS_ENOUGH_TO_SCROLL) {
            minOffset += mContentView.getHeight() - contentHeight;
            minOffset = Math.min(minOffset, 0);
        }
        return minOffset;
    }

    @Override
    public int getContentHeight() {
        final INestedScrollBottom b = (INestedScrollBottom) mContentView;
        final int bc = b.getContentHeight();
        if (bc == INestedScrollBottom.HEIGHT_IS_ENOUGH_TO_SCROLL || bc > mContentView.getHeight()) {
            return INestedScrollBottom.HEIGHT_IS_ENOUGH_TO_SCROLL;
        }
        final int bottomMargin = getContentBottomMargin();
        if (bc + mHeaderView.getHeight() + bottomMargin > getHeight()) {
            return INestedScrollBottom.HEIGHT_IS_ENOUGH_TO_SCROLL;
        }
        return mHeaderView.getHeight() + bc + bottomMargin;
    }


    protected int getHeaderStickyHeight() {
        return 0;
    }


    protected int getHeaderHeightLayoutParam() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    protected int getContentBottomMargin() {
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mContentView.measure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(heightSize - getHeaderStickyHeight() - getContentBottomMargin(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mHeaderView.layout(0, 0, mHeaderView.getMeasuredWidth(), mHeaderView.getMeasuredHeight());
        final int contentTop = mHeaderView.getBottom();
        mContentView.layout(0, contentTop, mContentView.getMeasuredWidth(), contentTop + mContentView.getMeasuredHeight());
        mHeaderViewOffsetHelper.onViewLayout();
        mContentViewOffsetHelper.onViewLayout();
        postCheckLayout();
    }

    public void postCheckLayout() {
        removeCallbacks(mCheckLayoutAction);
        post(mCheckLayoutAction);
    }

    public void checkLayout() {
        final int offsetCurrent = getOffsetCurrent();
        final int offsetRange = getOffsetRange();
        final INestedScrollBottom bottomView = (INestedScrollBottom) mContentView;
        if (offsetCurrent < offsetRange && bottomView.getCurrentScroll() > 0) {
            bottomView.consumeScroll(Integer.MIN_VALUE);
        }
    }

    private int offsetBy(int dyUnConsumed) {
        int canConsume = 0;
        FrameLayout.LayoutParams headerLp = (LayoutParams) mHeaderView.getLayoutParams();
        int minOffset = getMiniOffset();
        if (dyUnConsumed > 0) {
            canConsume = Math.min(mHeaderView.getTop() - minOffset, dyUnConsumed);
        } else if (dyUnConsumed < 0) {
            canConsume = Math.max(mHeaderView.getTop() - headerLp.topMargin, dyUnConsumed);
        }
        if (canConsume != 0) {
            mHeaderViewOffsetHelper.setTopAndBottomOffset(mHeaderViewOffsetHelper.getTopAndBottomOffset() - canConsume);
            mContentViewOffsetHelper.setTopAndBottomOffset(mContentViewOffsetHelper.getTopAndBottomOffset() - canConsume);
        }
        mOnScrollNotifier.notify(-mHeaderViewOffsetHelper.getTopAndBottomOffset(),
                mHeaderView.getHeight() + ((INestedScrollBottom) mContentView).getScrollOffsetRange());
        return dyUnConsumed - canConsume;
    }


    @Override
    public void consumeScroll(int dy) {
        if (dy == Integer.MAX_VALUE) {
            offsetBy(dy);
            ((INestedScrollBottom) mContentView).consumeScroll(Integer.MAX_VALUE);
            return;
        } else if (dy == Integer.MIN_VALUE) {
            ((INestedScrollBottom) mContentView).consumeScroll(Integer.MIN_VALUE);
            offsetBy(dy);
            return;
        }
        ((INestedScrollBottom) mContentView).consumeScroll(dy);
    }

    @Override
    public void smoothScrollYBy(int dy, int duration) {
        ((INestedScrollBottom) mContentView).smoothScrollYBy(dy, duration);
    }

    @Override
    public void stopScroll() {
        ((INestedScrollBottom) mContentView).stopScroll();
    }

    @Override
    public int getCurrentScroll() {
        return -mHeaderViewOffsetHelper.getTopAndBottomOffset() + ((INestedScrollBottom) mContentView).getCurrentScroll();
    }

    @Override
    public int getScrollOffsetRange() {
        if (getContentHeight() != HEIGHT_IS_ENOUGH_TO_SCROLL) return 0;
        return mHeaderView.getHeight() - getHeaderStickyHeight() + ((INestedScrollBottom) mContentView).getScrollOffsetRange();
    }

    @Override
    public void injectScrollNotifier(final OnScrollNotify notifier) {
        mOnScrollNotifier = notifier;
        if (mContentView instanceof INestedScrollBottom) {
            ((INestedScrollBottom) mContentView).injectScrollNotifier(new OnScrollNotify() {
                @Override
                public void notify(@Px int offset, @Px int range) {
                    notifier.notify(offset - mHeaderView.getTop(), range + mHeaderView.getHeight());
                }

                @Override
                public void onScrollStateChanged(@NonNull View view, @ScrollState int scrollState) {
                    notifier.onScrollStateChanged(view, scrollState);
                }
            });
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_SCROLL_OFFSET, mHeaderViewOffsetHelper.getTopAndBottomOffset());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            int offset = bundle.getInt(INSTANCE_SCROLL_OFFSET, 0);
            offset = NumberUtil.constrain(offset, getMiniOffset(), 0);
            mHeaderViewOffsetHelper.setTopAndBottomOffset(offset);
            mContentViewOffsetHelper.setTopAndBottomOffset(offset);
        } else super.onRestoreInstanceState(state);
    }
    // NestedScrollingChild2

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
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    // NestedScrollingChild

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
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    // NestedScrollingParent2

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target, type);
        stopNestedScroll(type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        final int remain = offsetBy(dyUnconsumed);
        dispatchNestedScroll(0, dyUnconsumed - remain, 0, remain, null, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        dispatchNestedPreScroll(dx, dy, consumed, null, type);
        int unconsumed = dy - consumed[1];
        if (unconsumed > 0) consumed[1] += unconsumed - offsetBy(unconsumed);
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        if (!consumed) {
            mViewFlinger.fling((int) velocityY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }


    private boolean isPointInHeaderBounds(int x, int y) {
        Utils.getDescendantRect(this, mHeaderView, mTempRect);
        return mTempRect.contains(x, y);
    }

    private void ensureVelocityTracker() {
        if (velocityTracker == null) velocityTracker = VelocityTracker.obtain();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchSlop < 0) touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE && isBeingDragged) return true;
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mViewFlinger.stop();
                isBeingDragged = false;
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                if (isPointInHeaderBounds(x, y)) {
                    lastMotionY = y;
                    this.activePointerId = ev.getPointerId(0);
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                final int actionIndex = ev.getActionIndex();
                return actionIndex != 0 && !isPointInHeaderBounds((int) ev.getX(), (int) ev.getY())
                        && isPointInHeaderBounds((int) ev.getX(actionIndex), (int) ev.getY(actionIndex));
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = this.activePointerId;
                // If we don't have a valid id, the touch down wasn't on content.
                if (activePointerId == INVALID_POINTER) break;
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) break;
                final int moveY = (int) ev.getY(pointerIndex);
                final int yDiff = Math.abs(moveY - lastMotionY);
                if (yDiff > touchSlop) {
                    isBeingDragged = true;
                    lastMotionY = moveY;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                this.activePointerId = INVALID_POINTER;
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
        }
        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (touchSlop < 0) touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) mNestedOffsetY = 0;
        final MotionEvent vtev = MotionEvent.obtain(ev);
        vtev.offsetLocation(0, mNestedOffsetY);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mViewFlinger.stop();
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                if (isPointInHeaderBounds(x, y)) {
                    lastMotionY = y;
                    activePointerId = ev.getPointerId(0);
                    ensureVelocityTracker();
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                } else return false;
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = ev.findPointerIndex(activePointerId);
                if (activePointerIndex == -1) return false;
                final int moveY = (int) ev.getY(activePointerIndex);
                int dy = lastMotionY - moveY;
                if (!isBeingDragged && Math.abs(dy) > touchSlop) {
                    isBeingDragged = true;
                    if (dy > 0) dy -= touchSlop;
                    else dy += touchSlop;
                }
                if (isBeingDragged) {
                    lastMotionY = moveY;
                    // the content view can scroll up, prevent drag
                    if (dy < 0 && ((INestedScrollBottom) mContentView).getCurrentScroll() > 0)
                        return true;
                    mScrollConsumed[0] = 0;
                    mScrollConsumed[1] = 0;
                    if (dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffset)) {
                        dy -= mScrollConsumed[1];
                        lastMotionY = moveY - mScrollOffset[1];
                        vtev.offsetLocation(0, mScrollOffset[1]);
                        mNestedOffsetY += mScrollOffset[1];
                    }
                    int unconsumed = offsetBy(dy);
                    if (dispatchNestedScroll(0, dy - unconsumed, 0, unconsumed, mScrollOffset, ViewCompat.TYPE_TOUCH)) {
                        lastMotionY = moveY - mScrollOffset[1];
                        vtev.offsetLocation(0, mScrollOffset[1]);
                        mNestedOffsetY += mScrollOffset[1];
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(vtev);
                    velocityTracker.computeCurrentVelocity(1000);
                    int yvel = -(int) (velocityTracker.getYVelocity(activePointerId) + 0.5f);
                    mViewFlinger.fling(yvel);
                }
                // $FALLTHROUGH
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                activePointerId = INVALID_POINTER;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
        }
        if (velocityTracker != null) velocityTracker.addMovement(vtev);
        vtev.recycle();
        return true;
    }

    class ViewFlinger implements Runnable {
        private int mLastFlingY;
        OverScroller mOverScroller;
        Interpolator mInterpolator = Interpolators.QUNITIC;

        // When set to true, postOnAnimation callbacks are delayed until the run method completes
        private boolean mEatRunOnAnimationRequest = false;

        // Tracks if postAnimationCallback should be re-attached when it is done
        private boolean mReSchedulePostAnimationCallback = false;

        ViewFlinger() {
            mOverScroller = new OverScroller(getContext(), Interpolators.QUNITIC);
        }

        @Override
        public void run() {
            mReSchedulePostAnimationCallback = false;
            mEatRunOnAnimationRequest = true;

            // Keep a local reference so that if it is changed during onAnimation method, it won't
            // cause unexpected behaviors
            final OverScroller scroller = mOverScroller;
            if (scroller.computeScrollOffset()) {
                final int y = scroller.getCurrY();
                int unconsumedY = y - mLastFlingY;
                mLastFlingY = y;
                INestedScrollBottom bottomView = (INestedScrollBottom) mContentView;
                boolean canScroll = unconsumedY <= 0 || bottomView.getCurrentScroll() < bottomView.getScrollOffsetRange();
                if (canScroll) {
                    if (!mChildHelper.hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
                        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
                    }
                    consumeScroll(unconsumedY);
                    postOnAnimation();
                } else stop();
            }

            mEatRunOnAnimationRequest = false;
            if (mReSchedulePostAnimationCallback) internalPostOnAnimation();
            else stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
        }

        void postOnAnimation() {
            if (mEatRunOnAnimationRequest) mReSchedulePostAnimationCallback = true;
            else internalPostOnAnimation();
        }

        private void internalPostOnAnimation() {
            removeCallbacks(this);
            ViewCompat.postOnAnimation(NestedScrollBottomFrameLayoutDelegate.this, this);

        }

        public void fling(int velocityY) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
            mLastFlingY = 0;
            // Because you can't define a custom interpolator for flinging, we should make sure we
            // reset ourselves back to the teh default interpolator in case a different call
            // changed our interpolator.
            if (mInterpolator != Interpolators.QUNITIC) {
                mInterpolator = Interpolators.QUNITIC;
                mOverScroller = new OverScroller(getContext(), Interpolators.QUNITIC);
            }
            mOverScroller.fling(0, 0, 0, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }


        public void stop() {
            removeCallbacks(this);
            mOverScroller.abortAnimation();
        }

    }


    @NonNull
    protected abstract View onCreateHeaderView();

    @NonNull
    protected abstract View onCreateContentView();

}
