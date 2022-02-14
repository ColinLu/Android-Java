package com.colin.library.android.utils.thread;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 16:22
 * <p>
 * 描述： 防止内存泄漏
 */
public final class WeakHandler extends Handler {
    private WeakReference<Activity> mContextRef;

    public WeakHandler(@NonNull Activity activity) {
        mContextRef = new WeakReference<>(activity);
    }

    @CallSuper
    @Override
    public void dispatchMessage(@NonNull Message msg) {
        final Activity activity = mContextRef == null ? null : mContextRef.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        super.dispatchMessage(msg);
    }
}
