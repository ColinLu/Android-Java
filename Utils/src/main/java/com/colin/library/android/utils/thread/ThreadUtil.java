package com.colin.library.android.utils.thread;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2021-12-25 08:24
 * <p>
 * 描述： TODO
 */
public final class ThreadUtil {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());         //主线程

    private ThreadUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void mainThread(@NonNull final Runnable runnable) {
        if (isMainThread()) runnable.run();
        else HANDLER.post(runnable);
    }

    /*主线程延迟*/
    public static void mainThreadDelayed(@NonNull final Runnable runnable, @IntRange(from = 0) long delay) {
        HANDLER.postDelayed(runnable, delay);
    }
}
