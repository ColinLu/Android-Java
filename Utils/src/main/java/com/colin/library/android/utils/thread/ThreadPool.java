package com.colin.library.android.utils.thread;

import androidx.annotation.IntRange;

import com.colin.library.android.annotation.PoolType;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 作者： ColinLu
 * 时间： 2020-12-11 10:31
 * <p>
 * 描述： 线程池
 */
public final class ThreadPool {
    public static final int THREAD_MAX = 128;
    public static final int THREAD_CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public static RejectedExecutionHandler getDefaultRejected() {
        return new ThreadPoolExecutor.DiscardOldestPolicy();
    }

    public static int getCoreCount(@PoolType int type) {
        switch (type) {
            case PoolType.FIXED:
            case PoolType.CPU:
                return THREAD_CPU_COUNT + 1;
            case PoolType.CACHED:
                return 0;
            case PoolType.IO:
                return (THREAD_CPU_COUNT << 1) + 1;
            case PoolType.SINGLE:
            case PoolType.CUSTOM:
            case PoolType.SCHEDULED:
            default:
                return 1;
        }
    }

    public static int getMaxCount(@PoolType int type) {
        switch (type) {
            case PoolType.FIXED:
                return THREAD_CPU_COUNT + 1;
            case PoolType.CACHED:
                return THREAD_MAX;
            case PoolType.IO:
            case PoolType.CPU:
                return (THREAD_CPU_COUNT << 1) + 1;
            case PoolType.SINGLE:
            case PoolType.CUSTOM:
            case PoolType.SCHEDULED:
            default:
                return 1;
        }
    }

    public static final class CustomThreadFactory extends AtomicLong implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        @PoolType
        private final int mPoolType;
        private final int mPriority;
        private final boolean mDaemon;


        public CustomThreadFactory(@PoolType int type, @IntRange(from = 1, to = 10) int priority) {
            this(type, priority, false);
        }

        public CustomThreadFactory(@PoolType int type, @IntRange(from = 1, to = 10) int priority,
                                   boolean daemon) {
            this.mPoolType = type;
            this.mPriority = priority;
            this.mDaemon = daemon;
        }


        @Override
        public Thread newThread(Runnable run) {
            final Thread thread = new Thread(run, threadName(getAndDecrement())) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            };
            thread.setDaemon(mDaemon);
            thread.setPriority(mPriority);
            return thread;
        }

        private String threadName(long dec) {
            switch (mPoolType) {
                case PoolType.SINGLE:
                    return "single-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case PoolType.FIXED:
                    return "fixed-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case PoolType.CACHED:
                    return "cache-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case PoolType.SCHEDULED:
                    return "scheduled-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case PoolType.IO:
                    return "io-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case PoolType.CPU:
                    return "cpu-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case PoolType.CUSTOM:
                default:
                    return "other-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
            }
        }
    }
}
