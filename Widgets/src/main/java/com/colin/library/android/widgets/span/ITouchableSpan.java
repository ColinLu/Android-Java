package com.colin.library.android.widgets.span;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2022-05-02 22:11
 * <p>
 * 描述： 点击Span 的Action
 */
public interface ITouchableSpan {
    void setPressed(boolean pressed);

    void onClick(@NonNull View view);
}
