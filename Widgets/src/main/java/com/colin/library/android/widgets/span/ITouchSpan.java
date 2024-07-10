package com.colin.library.android.widgets.span;

/**
 * 作者： ColinLu
 * 时间： 2022-05-02 21:47
 * <p>
 * 描述： 设置点击 Span
 */
public interface ITouchSpan {
    /**
     * 记录当前 Touch 事件对应的点是不是点在了 span 上面
     */
    void setTouchSpan(boolean touchSpan);
}
