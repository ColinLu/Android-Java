package com.colin.library.android.utils.thread;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.helper.ThreadHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 作者： ColinLu
 * 时间： 2019-09-26 11:21
 * <p>
 * 描述： 异步任务 子线程操作
 */
public abstract class Task<Result> implements Runnable {
    @IntDef({State.NEW, State.COMPLETING, State.CANCELLED, State.EXCEPTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int NEW = 0;           //新
        int COMPLETING = 1;    //进行中
        int CANCELLED = 2;     //取消
        int EXCEPTION = 3;     //异常
    }

    private volatile int mState = State.NEW;

    private final Callback<Result> mCallback;

    public Task(@Nullable final Callback<Result> callback) {
        mCallback = callback;
    }

    @Override
    public void run() {
        try {
            final Result t = doInBackground();
            if (mState != State.NEW) return;
            mState = State.COMPLETING;
            ThreadHelper.getInstance().post(() -> {
                if (null != mCallback) mCallback.onCall(t);
            });
        } catch (Exception th) {
            if (mState != State.NEW) return;
            mState = State.EXCEPTION;
        }
    }

    public void cancel() {
        mState = State.CANCELLED;
    }

    public boolean isDone() {
        return mState != State.NEW;
    }

    public boolean isCanceled() {
        return mState == State.CANCELLED;
    }


    ///////////////////////////////////////////////////////////////////////////
    //对外调用
    ///////////////////////////////////////////////////////////////////////////
    public abstract Result doInBackground();

    public interface Callback<T> {
        void onCall(T data);
    }

    /**
     * 异步任务操作 子线程操作
     *
     * @param task
     * @param <T>
     * @return
     */
    public static <T> Task<T> doAsync(@NonNull final Task<T> task) {
        ThreadHelper.getInstance().doAsync(task);
        return task;
    }


    /**
     * 主线程操作  延迟
     *
     * @param runnable
     * @param delayMillis 毫秒
     */
    public static void postDelayed(@NonNull final Runnable runnable, long delayMillis) {
        ThreadHelper.getInstance().postDelayed(runnable, delayMillis);
    }
}
