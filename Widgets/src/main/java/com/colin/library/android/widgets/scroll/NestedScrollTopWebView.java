package com.colin.library.android.widgets.scroll;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.ResourceUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 23:15
 * <p>
 * 描述： TODO
 */
public class NestedScrollTopWebView extends WebView implements INestedScrollTop {
    public static final String KEY_SCROLL_INFO = "KEY_SCROLL_INFO";

    private OnScrollNotify mScrollNotify;

    public NestedScrollTopWebView(@NonNull Context context) {
        super(context);
        setVerticalScrollBarEnabled(false);
    }

    public NestedScrollTopWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setVerticalScrollBarEnabled(false);
    }

    public NestedScrollTopWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public int consumeScroll(int yUnconsumed) {
        // compute the consumed value
        int scrollY = getScrollY();
        int maxScrollY = getScrollOffsetRange();
        // the scrollY may be negative or larger than scrolling range
        scrollY = Math.max(0, Math.min(scrollY, maxScrollY));
        int dy = 0;
        if (yUnconsumed < 0) dy = Math.max(yUnconsumed, -scrollY);
        else if (yUnconsumed > 0) dy = Math.min(yUnconsumed, maxScrollY - scrollY);
        scrollBy(0, dy);
        return yUnconsumed - dy;
    }

    @Override
    public int getCurrentScroll() {
        int scrollY = getScrollY();
        int scrollRange = getScrollOffsetRange();
        return Math.max(0, Math.min(scrollY, scrollRange));
    }

    @Override
    public int getScrollOffsetRange() {
        return computeVerticalScrollRange() - getHeight();
    }

    @Override
    public void injectScrollNotifier(OnScrollNotify notifier) {
        mScrollNotify = notifier;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollNotify != null) mScrollNotify.notify(getCurrentScroll(), getScrollOffsetRange());

    }

    @Override
    public void saveScrollInfo(@NonNull Bundle bundle) {
        bundle.putInt(KEY_SCROLL_INFO, getScrollY());
    }

    @Override
    public void restoreScrollInfo(@NonNull Bundle bundle) {
        int scrollY = ResourceUtil.px2dp(bundle.getInt(KEY_SCROLL_INFO, 0));
        exec("javascript:scrollTo(0, " + scrollY + ")");
    }

    private void exec(final String jsCode) {
        evaluateJavascript(jsCode, null);
    }
}
