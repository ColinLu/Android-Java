package com.colin.library.android.utils.thread;

import android.os.Process;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 作者： ColinLu
 * 时间： 2020-12-11 10:31
 * <p>
 * 描述： 线程池
 */
public final class ThreadPool {
    // 线程池类型  IO流网络请求  CPU复杂运算操作
    public static final int POOL_CUSTOM = 0;
    public static final int POOL_SINGLE = 1;
    public static final int POOL_FIXED = 2;
    public static final int POOL_CACHED = 3;
    public static final int POOL_SCHEDULED = 4;
    public static final int POOL_IO = 5;
    public static final int POOL_CPU = 6;

    @IntDef({POOL_CUSTOM, POOL_SINGLE, POOL_FIXED, POOL_CACHED, POOL_SCHEDULED, POOL_IO, POOL_CPU})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PoolType {
    }

    /*取消*/
    public interface OnCancelListener {
        void cancel();
    }

    /*操作*/
    public interface OnFutureListener<T> {
        void future(Future<T> future);
    }

    private static final String THREAD_NAME = "thread-pool";
    private static final long KEEP_ALIVE_TIME = 10L;

    private static volatile ThreadPool sThreadPool;
    private final ExecutorService mExecutor;
    private final ResourceCounter mCpuCounter = ResourceCounter.getCPU();
    private final ResourceCounter mIoCounter = ResourceCounter.getIO();


    private ThreadPool() {
        mExecutor = new ThreadPoolExecutor(ThreadUtil.getCoreCount(POOL_CPU), ThreadUtil.getMaxCount(POOL_CPU), KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new PriorityThreadFactory(THREAD_NAME, 10));
    }


    ///////////////////////////////////////////////////////////////////////////
    // 对外公开方法
    ///////////////////////////////////////////////////////////////////////////
    public static ThreadPool getInstance() {
        if (sThreadPool == null) {
            synchronized (ThreadPool.class) {
                if (sThreadPool == null) sThreadPool = new ThreadPool();
            }
        }
        return sThreadPool;
    }

    public <T> Future<T> submit(Work<T> work) {
        return submit(work, null);
    }

    public <T> Future<T> submit(Work<T> work, OnFutureListener<T> listener) {
        final Worker<T> worker = new Worker<T>(work, listener);
        mExecutor.execute(worker);
        return worker;
    }


    ///////////////////////////////////////////////////////////////////////////
    // 辅助类
    ///////////////////////////////////////////////////////////////////////////
    private static class PriorityThreadFactory implements ThreadFactory {

        private final AtomicInteger mNumber = new AtomicInteger();
        private final String mName;
        private final int mPriority;

        public PriorityThreadFactory(@NonNull String name, @IntRange(from = 1, to = 10) int priority) {
            this.mName = name;
            this.mPriority = priority;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, mName + '-' + mNumber.getAndIncrement()) {
                @Override
                public void run() {
                    Process.setThreadPriority(mPriority);
                    super.run();
                }
            };
        }
    }

    private static class ResourceCounter {
        public static ResourceCounter getCPU() {
            return new ResourceCounter(2);
        }

        public static ResourceCounter getIO() {
            return new ResourceCounter(2);
        }

        public int mCount;

        public ResourceCounter(int count) {
            mCount = count;
        }
    }


    private class Worker<T> implements Runnable, Future<T>, Task {

        private final Work<T> mWork;
        private final OnFutureListener<T> mListener;

        private OnCancelListener mCancelListener;
        private ResourceCounter mWaitOnResource;
        private volatile boolean mIsCancelled;
        private volatile boolean mIsDone;
        @PoolType
        private int mPoolType;
        private T mResult;

        public Worker(@NonNull Work<T> work, @Nullable OnFutureListener<T> listener) {
            this.mWork = work;
            this.mListener = listener;
        }

        /*Runnable*/
        public void run() {
            T result = null;
            if (setPoolType(POOL_CPU)) {
                try {
                    result = mWork.run(this);
                } catch (Throwable t) {
                    LogUtil.log(t);
                }
            }

            synchronized (this) {
                setPoolType(POOL_CUSTOM);
                mResult = result;
                mIsDone = true;
                notifyAll();
            }
            if (mListener != null) mListener.future(this);
        }

        /*Future*/
        public synchronized T get() {
            while (!mIsDone) {
                try {
                    wait();
                } catch (Exception e) {
                    LogUtil.log(e);
                }
            }
            return mResult;
        }

        public synchronized boolean isCancelled() {
            return mIsCancelled;
        }

        public synchronized boolean isDone() {
            return mIsDone;
        }

        public synchronized void cancel() {
            if (mIsCancelled) return;
            mIsCancelled = true;
            if (mWaitOnResource != null) {
                synchronized (mWaitOnResource) {
                    mWaitOnResource.notifyAll();
                }
            }
            if (mCancelListener != null) {
                mCancelListener.cancel();
            }
        }


        public void waitFor() {
            get();
        }

        /*Task*/
        @Override
        public boolean setPoolType(int mode) {
            // Release old resource
            ResourceCounter rc = modeToCounter(mPoolType);
            if (rc != null) releaseResource(rc);
            mPoolType = POOL_CUSTOM;

            // Acquire new resource
            rc = modeToCounter(mode);
            if (rc != null) {
                if (!acquireResource(rc)) {
                    return false;
                }
                mPoolType = mode;
            }

            return true;
        }

        @Override
        public synchronized void setCancelListener(OnCancelListener listener) {
            mCancelListener = listener;
            if (mIsCancelled && mCancelListener != null) {
                mCancelListener.cancel();
            }
        }


        @Nullable
        private ResourceCounter modeToCounter(int mode) {
            if (mode == POOL_CPU) return mCpuCounter;
            else if (mode == POOL_IO) return mIoCounter;
            else return null;
        }

        private boolean acquireResource(@NonNull ResourceCounter counter) {
            while (true) {
                synchronized (this) {
                    if (mIsCancelled) {
                        mWaitOnResource = null;
                        return false;
                    }
                    mWaitOnResource = counter;
                }

                synchronized (counter) {
                    if (counter.mCount > 0) {
                        counter.mCount--;
                        break;
                    } else {
                        try {
                            counter.wait();
                        } catch (InterruptedException ex) {
                            // ignore.
                        }
                    }
                }
            }
            synchronized (this) {
                mWaitOnResource = null;
            }
            return true;
        }

        private void releaseResource(@NonNull ResourceCounter counter) {
            synchronized (counter) {
                counter.mCount++;
                counter.notifyAll();
            }
        }
    }


    public interface Work<T> {
        T run(Task task);
    }

    public interface Future<T> {
        T get();

        boolean isCancelled();

        boolean isDone();

        void cancel();

        void waitFor();
    }

    public interface Task {
        boolean setPoolType(@PoolType int poolType);

        boolean isCancelled();

        void setCancelListener(@Nullable OnCancelListener listener);
    }
}
