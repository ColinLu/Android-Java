package com.colin.library.android.widgets.span;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.colin.library.android.utils.ResourceUtil;
import com.colin.library.android.widgets.R;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2022-05-02 22:09
 * <p>
 * 描述： 处理
 */
public final class TouchSpanHelper {
    private WeakReference<ITouchableSpan> mTouchSpanRef;



    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        ITouchableSpan span = null;
        ITouchableSpan cache = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                span = getPressedSpan(textView, spannable, event);
                if (span != null) {
                    span.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(span), spannable.getSpanEnd(span));
                    mTouchSpanRef = new WeakReference<>(span);
                }
                if (textView instanceof ITouchSpan)
                    ((ITouchSpan) textView).setTouchSpan(span != null);
                return span != null;
            case MotionEvent.ACTION_MOVE:
                span = getPressedSpan(textView, spannable, event);
                cache = mTouchSpanRef == null ? null : mTouchSpanRef.get();
                if (cache != null && cache != span) {
                    cache.setPressed(false);
                    mTouchSpanRef = null;
                    cache = null;
                    Selection.removeSelection(spannable);
                }
                if (textView instanceof ITouchSpan)
                    ((ITouchSpan) textView).setTouchSpan(cache != null);
                return cache != null;
            case MotionEvent.ACTION_UP:
                boolean touchSpan = false;
                cache = mTouchSpanRef == null ? null : mTouchSpanRef.get();
                if (cache != null) {
                    touchSpan = true;
                    cache.setPressed(false);
                    cache.onClick(textView);
                }
                mTouchSpanRef = null;
                Selection.removeSelection(spannable);
                if (textView instanceof ITouchSpan) ((ITouchSpan) textView).setTouchSpan(touchSpan);
                return touchSpan;
            default:
                cache = mTouchSpanRef == null ? null : mTouchSpanRef.get();
                if (cache != null) cache.setPressed(false);
                if (textView instanceof ITouchSpan) ((ITouchSpan) textView).setTouchSpan(false);
                mTouchSpanRef = null;
                Selection.removeSelection(spannable);
                return false;
        }
    }

    public ITouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();
        x += textView.getScrollX();
        y += textView.getScrollY();
        final Layout layout = textView.getLayout();
        final int line = layout.getLineForVertical(y);

        /*
         * BugFix: https://issuetracker.google.com/issues/113348914
         */
        try {
            int off = layout.getOffsetForHorizontal(line, x);
            // 实际上没点到任何内容
            if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) off = -1;
            final ITouchableSpan[] link = spannable.getSpans(off, off, ITouchableSpan.class);
            return link.length > 0 ? link[0] : null;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }


}
