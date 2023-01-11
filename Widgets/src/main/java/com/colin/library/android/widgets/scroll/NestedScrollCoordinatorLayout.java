package com.colin.library.android.widgets.scroll;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.colin.library.android.utils.NumberUtil;
import com.colin.library.android.widgets.annotation.ScrollState;
import com.colin.library.android.widgets.bar.DragScrollBar;
import com.colin.library.android.widgets.behavior.BottomAreaBehavior;
import com.colin.library.android.widgets.behavior.TopAreaBehavior;

import java.util.ArrayList;
import java.util.List;

public class NestedScrollCoordinatorLayout extends CoordinatorLayout implements
        TopAreaBehavior.Callback, DragScrollBar.Callback {
    public static final String INSTANCE_STATE = "INSTANCE_STATE";
    public static final String INSTANCE_SCROLL_OFFSET = "INSTANCE_SCROLL_OFFSET";
    private final List<OnScrollListener> mOnScrollListeners = new ArrayList<>();
    private final Runnable mCheckLayoutAction = this::checkLayout;

    private INestedScrollTop mTopView;
    private INestedScrollBottom mBottomView;
    private TopAreaBehavior mTopAreaBehavior;
    private BottomAreaBehavior mBottomAreaBehavior;


    private boolean mKeepBottomAreaStableWhenCheckLayout = false;
    private DragScrollBar mDragScrollBar;
    private boolean mEnableScrollBarFadeInOut = true;
    private boolean mIsDraggableScrollBarEnabled = false;
    private boolean mIsDismissDownEvent = false;
    private int mCurrentScrollState = ScrollState.SCROLL_STATE_IDLE;
    private float mDismissDownY = 0;
    private int mTouchSlap = -1;

    public NestedScrollCoordinatorLayout(@NonNull Context context) {
        this(context, null);
    }

    public NestedScrollCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void ensureScrollBar() {
        if (mDragScrollBar == null) {
            mDragScrollBar = new DragScrollBar(getContext());
            mDragScrollBar.setEnableFadeInAndOut(mEnableScrollBarFadeInOut);
            mDragScrollBar.setCallback(this);
            final LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.RIGHT;
            addView(mDragScrollBar, lp);
        }
    }

    public void setDraggableScrollBarEnabled(boolean draggableScrollBarEnabled) {
        if (mIsDraggableScrollBarEnabled != draggableScrollBarEnabled) {
            mIsDraggableScrollBarEnabled = draggableScrollBarEnabled;
            if (mIsDraggableScrollBarEnabled && !mEnableScrollBarFadeInOut) {
                ensureScrollBar();
                mDragScrollBar.setPercent(getCurrentScrollPercent());
                mDragScrollBar.awakenScrollBar();
            }
            if (mDragScrollBar != null) {
                mDragScrollBar.setVisibility(draggableScrollBarEnabled ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void setEnableScrollBarFadeInOut(boolean enableScrollBarFadeInOut) {
        if (mEnableScrollBarFadeInOut != enableScrollBarFadeInOut) {
            mEnableScrollBarFadeInOut = enableScrollBarFadeInOut;
            if (mIsDraggableScrollBarEnabled && !mEnableScrollBarFadeInOut) {
                ensureScrollBar();
                mDragScrollBar.setPercent(getCurrentScrollPercent());
                mDragScrollBar.awakenScrollBar();
            }
            if (mDragScrollBar != null) {
                mDragScrollBar.setEnableFadeInAndOut(enableScrollBarFadeInOut);
                mDragScrollBar.invalidate();
            }
        }
    }


    @Override
    public void onDragStarted() {
        stopScroll();
    }

    @Override
    public void onDragToPercent(float percent) {
        int targetScroll = (int) (getScrollRange() * percent);
        scrollBy(targetScroll - getCurrentScroll());
    }

    @Override
    public void onDragEnd() {

    }

    public int getCurrentScroll() {
        int currentScroll = 0;
        if (mTopView != null) currentScroll += mTopView.getCurrentScroll();
        currentScroll += getOffsetCurrent();
        if (mBottomView != null) currentScroll += mBottomView.getCurrentScroll();
        return currentScroll;
    }

    public int getScrollRange() {
        int totalRange = 0;
        if (mTopView != null) totalRange += mTopView.getScrollOffsetRange();
        totalRange += getOffsetRange();
        if (mBottomView != null) totalRange += mBottomView.getScrollOffsetRange();
        return totalRange;
    }

    public float getCurrentScrollPercent() {
        int scrollRange = getScrollRange();
        if (scrollRange == 0) return 0;
        return getCurrentScroll() * 1f / scrollRange;
    }


    public void addOnScrollListener(@NonNull OnScrollListener onScrollListener) {
        if (!mOnScrollListeners.contains(onScrollListener))
            mOnScrollListeners.add(onScrollListener);

    }

    public void removeOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListeners.remove(onScrollListener);
    }

    public void setKeepBottomAreaStableWhenCheckLayout(boolean keepBottomAreaStableWhenCheckLayout) {
        mKeepBottomAreaStableWhenCheckLayout = keepBottomAreaStableWhenCheckLayout;
    }

    public boolean isKeepBottomAreaStableWhenCheckLayout() {
        return mKeepBottomAreaStableWhenCheckLayout;
    }

    public void setTopAreaView(View topView, @Nullable LayoutParams layoutParams) {
        if (!(topView instanceof INestedScrollTop)) {
            throw new IllegalStateException("topView must implement from IQMUIContinuousNestedTopView");
        }
        if (mTopView != null) {
            removeView(((View) mTopView));
        }
        mTopView = (INestedScrollTop) topView;
        mTopView.injectScrollNotifier(new INestedScroll.OnScrollNotify() {
            @Override
            public void notify(@Px int offset, @Px int range) {
                int offsetCurrent = mTopAreaBehavior == null ? 0 : -mTopAreaBehavior.getTopAndBottomOffset();
                int bottomCurrent = mBottomView == null ? 0 : mBottomView.getCurrentScroll();
                int bottomRange = mBottomView == null ? 0 : mBottomView.getScrollOffsetRange();
                dispatchScroll(offset, range, offsetCurrent, getOffsetRange(), bottomCurrent, bottomRange);
            }
        });
        if (layoutParams == null) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        Behavior<?> behavior = layoutParams.getBehavior();
        if (behavior instanceof TopAreaBehavior) {
            mTopAreaBehavior = (TopAreaBehavior) behavior;
        } else {
            mTopAreaBehavior = new TopAreaBehavior(getContext());
            layoutParams.setBehavior(mTopAreaBehavior);
        }
        mTopAreaBehavior.setCallback(this);
        addView(topView, 0, layoutParams);
    }

    public INestedScrollTop getTopView() {
        return mTopView;
    }

    public INestedScrollBottom getBottomView() {
        return mBottomView;
    }

    public TopAreaBehavior getTopAreaBehavior() {
        return mTopAreaBehavior;
    }

    public BottomAreaBehavior getBottomAreaBehavior() {
        return mBottomAreaBehavior;
    }

    public void setBottomAreaView(View bottomView, @Nullable LayoutParams layoutParams) {
        if (!(bottomView instanceof INestedScrollBottom)) {
            throw new IllegalStateException("bottomView must implement from INestedScrollBottom");
        }
        if (mBottomView != null) removeView(((View) mBottomView));

        mBottomView = (INestedScrollBottom) bottomView;
        mBottomView.injectScrollNotifier(new INestedScrollBottom.OnScrollNotify() {
            @Override
            public void notify(@Px int offset, @Px int range) {
                int topCurrent = mTopView == null ? 0 : mTopView.getCurrentScroll();
                int topRange = mTopView == null ? 0 : mTopView.getScrollOffsetRange();
                int offsetCurrent = mTopAreaBehavior == null ? 0 : -mTopAreaBehavior.getTopAndBottomOffset();
                dispatchScroll(topCurrent, topRange, offsetCurrent, getOffsetRange(), offset, range);
            }

            @Override
            public void onScrollStateChanged(@NonNull View view, @ScrollState int scrollState) {
                dispatchScrollStateChanged(scrollState, false);
            }
        });
        if (layoutParams == null) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        final Behavior<?> behavior = layoutParams.getBehavior();
        if (behavior instanceof BottomAreaBehavior)
            mBottomAreaBehavior = (BottomAreaBehavior) behavior;
        else {
            mBottomAreaBehavior = new BottomAreaBehavior();
            layoutParams.setBehavior(mBottomAreaBehavior);
        }
        addView(bottomView, 0, layoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        postCheckLayout();
    }

    public void postCheckLayout() {
        removeCallbacks(mCheckLayoutAction);
        post(mCheckLayoutAction);
    }

    public void checkLayout() {
        if (mTopView == null || mBottomView == null) return;

        int topCurrent = mTopView.getCurrentScroll();
        int topRange = mTopView.getScrollOffsetRange();
        int offsetCurrent = -mTopAreaBehavior.getTopAndBottomOffset();
        int offsetRange = getOffsetRange();

        if (offsetRange <= 0) return;


        if (offsetCurrent >= offsetRange || (offsetCurrent > 0 && mKeepBottomAreaStableWhenCheckLayout)) {
            mTopView.consumeScroll(Integer.MAX_VALUE);
            if (mBottomView.getCurrentScroll() > 0)
                mTopAreaBehavior.setTopAndBottomOffset(-offsetRange);
            return;
        }

        if (mBottomView.getCurrentScroll() > 0) mBottomView.consumeScroll(Integer.MIN_VALUE);


        if (topCurrent < topRange && offsetCurrent > 0) {
            int remain = topRange - topCurrent;
            if (offsetCurrent >= remain) {
                mTopView.consumeScroll(Integer.MAX_VALUE);
                mTopAreaBehavior.setTopAndBottomOffset(remain - offsetCurrent);
            } else {
                mTopView.consumeScroll(offsetCurrent);
                mTopAreaBehavior.setTopAndBottomOffset(0);
            }
        }
    }

    public void scrollBottomViewToTop() {
        if (mTopView != null) {
            mTopView.consumeScroll(Integer.MAX_VALUE);
        }

        if (mBottomView != null) {
            mBottomView.consumeScroll(Integer.MIN_VALUE);
            int contentHeight = mBottomView.getContentHeight();
            if (contentHeight != INestedScrollBottom.HEIGHT_IS_ENOUGH_TO_SCROLL) {
                mTopAreaBehavior.setTopAndBottomOffset(Math.min(0, getHeight() - contentHeight - ((View) mTopView).getHeight()));
            } else {
                mTopAreaBehavior.setTopAndBottomOffset(getHeight() - ((View) mBottomView).getHeight() - ((View) mTopView).getHeight());
            }
        }
    }

    private void dispatchScroll(int topCurrent, int topRange, int offsetCurrent, int offsetRange,
                                int bottomCurrent, int bottomRange) {
        if (mIsDraggableScrollBarEnabled) {
            ensureScrollBar();
            mDragScrollBar.setPercent(getCurrentScrollPercent());
            mDragScrollBar.awakenScrollBar();

        }
        for (OnScrollListener onScrollListener : mOnScrollListeners) {
            onScrollListener.onScroll(this, topCurrent, topRange, offsetCurrent, offsetRange,
                    bottomCurrent, bottomRange);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyUnconsumed > 0 && getCurrentScroll() >= getScrollRange()) {
            // RecyclerView does not stop scroller when over scroll with NestedScrollingParent
            stopScroll();
        }
    }

    private void dispatchScrollStateChanged(int newScrollState, boolean fromTopBehavior) {
        for (OnScrollListener onScrollListener : mOnScrollListeners) {
            onScrollListener.onScrollStateChanged(this, newScrollState, fromTopBehavior);
        }
        mCurrentScrollState = newScrollState;
    }

    public void scrollBy(int dy) {
        if ((dy > 0 || mBottomView == null) && mTopAreaBehavior != null) {
            mTopAreaBehavior.scroll(this, ((View) mTopView), dy);
        } else if (dy != 0 && mBottomView != null) {
            mBottomView.consumeScroll(dy);
        }
    }

    public void smoothScrollBy(int dy, int duration) {
        if (dy == 0) {
            return;
        }
        if ((dy > 0 || mBottomView == null) && mTopAreaBehavior != null) {
            mTopAreaBehavior.smoothScrollBy(this, ((View) mTopView), dy, duration);
        } else if (mBottomView != null) {
            mBottomView.smoothScrollYBy(dy, duration);
        }
    }

    public void stopScroll() {
        if (mBottomView != null) {
            mBottomView.stopScroll();
        }
        if (mTopAreaBehavior != null) {
            mTopAreaBehavior.stopFlingOrScroll();
        }
    }

    public void scrollToTop() {
        if (mBottomView != null) {
            mBottomView.consumeScroll(Integer.MIN_VALUE);
        }
        if (mTopView != null) {
            mTopAreaBehavior.setTopAndBottomOffset(0);
            mTopView.consumeScroll(Integer.MIN_VALUE);
        }
    }


    public void scrollToBottom() {
        if (mTopView != null) {
            // consume the max value
            mTopView.consumeScroll(Integer.MAX_VALUE);
            if (mBottomView != null) {
                int contentHeight = mBottomView.getContentHeight();
                if (contentHeight != INestedScrollBottom.HEIGHT_IS_ENOUGH_TO_SCROLL) {
                    // bottomView can not scroll
                    View topView = (View) mTopView;
                    if (topView.getHeight() + contentHeight < getHeight()) {
                        mTopAreaBehavior.setTopAndBottomOffset(0);
                    } else {
                        mTopAreaBehavior.setTopAndBottomOffset(getHeight() - contentHeight - ((View) mTopView).getHeight());
                    }
                } else {
                    mTopAreaBehavior.setTopAndBottomOffset(getHeight() - ((View) mBottomView).getHeight() - ((View) mTopView).getHeight());
                }
            }
        }
        if (mBottomView != null) {
            mBottomView.consumeScroll(Integer.MAX_VALUE);
        }
    }

    public int getOffsetCurrent() {
        return mTopAreaBehavior == null ? 0 : -mTopAreaBehavior.getTopAndBottomOffset();
    }

    public int getOffsetRange() {
        if (mTopView == null && mBottomView == null) {
            return 0;
        }
        if (mBottomView == null) {
            return Math.max(0, ((View) mTopView).getHeight() - getHeight());
        }
        if (mTopView == null) {
            return 0;
        }
        int contentHeight = mBottomView.getContentHeight();
        if (contentHeight != INestedScrollBottom.HEIGHT_IS_ENOUGH_TO_SCROLL) {
            return Math.max(0, ((View) mTopView).getHeight() + contentHeight - getHeight());
        }
        return Math.max(0, ((View) mTopView).getHeight() + ((View) mBottomView).getHeight() - getHeight());
    }

    @Override
    public void onTopAreaOffset(int offset) {
        int topCurrent = mTopView == null ? 0 : mTopView.getCurrentScroll();
        int topRange = mTopView == null ? 0 : mTopView.getScrollOffsetRange();
        int bottomCurrent = mBottomView == null ? 0 : mBottomView.getCurrentScroll();
        int bottomRange = mBottomView == null ? 0 : mBottomView.getScrollOffsetRange();
        dispatchScroll(topCurrent, topRange, -offset, getOffsetRange(), bottomCurrent, bottomRange);
    }

    @Override
    public void onTopBehaviorTouchBegin() {
        dispatchScrollStateChanged(ScrollState.SCROLL_STATE_DRAGGING, true);
    }

    @Override
    public void onTopBehaviorTouchEnd() {
        dispatchScrollStateChanged(ScrollState.SCROLL_STATE_IDLE, true);
    }

    @Override
    public void onTopBehaviorFlingOrScrollStart() {
        dispatchScrollStateChanged(ScrollState.SCROLL_STATE_SETTLING, true);
    }

    @Override
    public void onTopBehaviorFlingOrScrollEnd() {
        dispatchScrollStateChanged(ScrollState.SCROLL_STATE_IDLE, true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (mCurrentScrollState != ScrollState.SCROLL_STATE_IDLE) {
                // must stop scroll and not use the current down event.
                // this is worked when topView scroll to bottomView or bottomView scroll to topView.
                stopScroll();
                mIsDismissDownEvent = true;
                mDismissDownY = ev.getY();
                if (mTouchSlap < 0) {
                    mTouchSlap = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                }
                return true;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE && mIsDismissDownEvent) {
            if (Math.abs(ev.getY() - mDismissDownY) > mTouchSlap) {
                MotionEvent down = MotionEvent.obtain(ev);
                down.setAction(MotionEvent.ACTION_DOWN);
                down.offsetLocation(0, mDismissDownY - ev.getY());
                super.dispatchTouchEvent(down);
                down.recycle();
            } else {
                return true;
            }
        }
        mIsDismissDownEvent = false;
        return super.dispatchTouchEvent(ev);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_SCROLL_OFFSET, getOffsetCurrent());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            final int offset = bundle.getInt(INSTANCE_SCROLL_OFFSET, 0);
            mTopAreaBehavior.setTopAndBottomOffset(NumberUtil.constrain(-offset, -getOffsetRange(), 0));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else super.onRestoreInstanceState(state);
    }


    public interface OnScrollListener {

        void onScroll(@NonNull NestedScrollCoordinatorLayout scrollLayout, int topCurrent, int topRange,
                      int offsetCurrent, int offsetRange, int bottomCurrent, int bottomRange);

        void onScrollStateChanged(@NonNull NestedScrollCoordinatorLayout scrollLayout, int newScrollState, boolean fromTopBehavior);
    }
}
