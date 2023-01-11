package com.colin.library.android.media.task;


import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

/**
 * 作者： ColinLu
 * 时间： 2019-12-16 11:42
 * <p>
 * 描述： 扫描相册  接口回调
 */
public abstract class OnTaskListener<T> {
    @UiThread
    public void start() {
    }

    @UiThread
    public void fail(@Nullable String reason) {
    }

    @UiThread
    public abstract void result(@Nullable T result);
}
