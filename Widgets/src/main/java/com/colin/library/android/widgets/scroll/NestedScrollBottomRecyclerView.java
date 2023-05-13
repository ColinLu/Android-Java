package com.colin.library.android.widgets.scroll;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者： ColinLu
 * 时间： 2023-01-01 00:53
 * <p>
 * 描述： TODO
 */
public class NestedScrollBottomRecyclerView extends RecyclerView implements INestedScrollBottom {
    public static final String INSTANCE_STATE = "INSTANCE_STATE";
    public static final String INSTANCE_SCROLL_OFFSET = "INSTANCE_SCROLL_OFFSET";
    public static final String INSTANCE_SCROLL_POSITION = "INSTANCE_SCROLL_POSITION";
    private INestedScroll.OnScrollNotify mScrollNotify;
    private final int[] mScrollConsumed = new int[2];

    public NestedScrollBottomRecyclerView(@NonNull Context context) {
        super(context);
        ListView
        init();
    }

    public NestedScrollBottomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedScrollBottomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setVerticalScrollBarEnabled(false);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (mScrollNotify != null)
                    mScrollNotify.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (mScrollNotify != null) {
                    mScrollNotify.notify(recyclerView.computeVerticalScrollOffset(),
                            Math.max(0, recyclerView.computeVerticalScrollRange() - recyclerView.getHeight()));
                }
            }
        });
    }

    @Override
    public void consumeScroll(int yUnconsumed) {
        if (yUnconsumed == Integer.MIN_VALUE) {
            if (canScrollVertically(-1)) scrollToPosition(0);
        } else if (yUnconsumed == Integer.MAX_VALUE) {
            if (canScrollVertically(1)) {
                final Adapter<?> adapter = getAdapter();
                if (adapter != null) scrollToPosition(adapter.getItemCount() - 1);
            }
        } else {
            boolean reStartNestedScroll = false;
            if (!hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)) {
                // the scrollBy use ViewCompat.TYPE_TOUCH to handle nested scroll...
                reStartNestedScroll = true;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);

                // and scrollBy only call dispatchNestedScroll, not call dispatchNestedPreScroll
                mScrollConsumed[0] = 0;
                mScrollConsumed[1] = 0;
                dispatchNestedPreScroll(0, yUnconsumed, mScrollConsumed, null, ViewCompat.TYPE_TOUCH);
                yUnconsumed -= mScrollConsumed[1];
            }
            scrollBy(0, yUnconsumed);
            if (reStartNestedScroll) stopNestedScroll(ViewCompat.TYPE_TOUCH);
        }
    }

    @Override
    public int getContentHeight() {
        final Adapter<?> adapter = getAdapter();
        if (adapter == null) return 0;

        final LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return 0;

        final int scrollRange = this.computeVerticalScrollRange();
        if (scrollRange > getHeight()) return HEIGHT_IS_ENOUGH_TO_SCROLL;

        return scrollRange;
    }

    @Override
    public void injectScrollNotifier(@Nullable OnScrollNotify notifier) {
        mScrollNotify = notifier;
    }

    @Override
    public int getCurrentScroll() {
        return computeVerticalScrollOffset();
    }

    @Override
    public int getScrollOffsetRange() {
        return Math.max(0, computeVerticalScrollRange() - getHeight());
    }

    @Override
    public void smoothScrollYBy(int dy, int duration) {
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        smoothScrollBy(0, dy, null);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final LayoutManager manager = getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            final LinearLayoutManager lm = (LinearLayoutManager) manager;
            final Bundle bundle = new Bundle();
            final int pos = lm.findFirstVisibleItemPosition();
            final View firstView = lm.findViewByPosition(pos);
            final int offset = firstView == null ? 0 : firstView.getTop();
            bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
            bundle.putInt(INSTANCE_SCROLL_POSITION, pos);
            bundle.putInt(INSTANCE_SCROLL_OFFSET, offset);
            return bundle;
        }
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle && getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
            final Bundle bundle = (Bundle) state;
            final int pos = bundle.getInt(INSTANCE_SCROLL_POSITION, 0);
            final int offset = bundle.getInt(INSTANCE_SCROLL_OFFSET, 0);
            manager.scrollToPositionWithOffset(pos, offset);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            if (mScrollNotify != null)
                mScrollNotify.notify(getCurrentScroll(), getScrollOffsetRange());
        } else super.onRestoreInstanceState(state);
    }

}
