package com.colin.library.android.widgets.span;

import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 作者： ColinLu
 * 时间： 2022-05-02 22:54
 * <p>
 * 描述： TODO
 */
public class ScrollMovementMethod extends ScrollingMovementMethod {
    private static final TouchSpanHelper HELPER = new TouchSpanHelper();
    private static ScrollMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null) sInstance = new ScrollMovementMethod();
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        return HELPER.onTouchEvent(widget, buffer, event) || Touch.onTouchEvent(widget, buffer, event);
    }
}
