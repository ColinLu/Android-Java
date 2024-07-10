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
    @IntDef({State.NEW, State.ING, State.CANCELLED, State.EXCEPTION, State.FINISH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int NEW = 0;            //新
        int ING = 1;            //进行中
        int CANCELLED = 2;      //取消
        int EXCEPTION = 3;      //异常
        int FINISH = 4;         //完成
    }

    private volatile int mState = State.NEW;

    private final Action<Result> mAction;

    public Task(@Nullable final Action<Result> action) {
        mAction = action;
    }

    @Override
    public void run() {
        try {
            final Result t = doInBackground();
            if (mState != State.NEW) return;
            mState = State.ING;
            ThreadHelper.getInstance().post(() -> {
                if (null != mAction) mAction.call(t);
            });
        } catch (Exception th) {
            if (mState != State.NEW) return;
            mState = State.EXCEPTION;
        } finally {
            if (mState != State.NEW) {
                mState = State.FINISH;
            }
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

    public interface Action<T> {
        void call(T data);
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
