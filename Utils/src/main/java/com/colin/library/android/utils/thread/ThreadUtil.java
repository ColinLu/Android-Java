package com.colin.library.android.utils.thread;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 作者： ColinLu
 * 时间： 2020-10-28 14:23
 * <p>
 * https://www.jianshu.com/p/a27416b7f01f
 * 描述： 阻塞系数 = 阻塞时间 / 总的运行时间，如果任务有 50% 的时间处于阻塞状态，则阻塞系数为0.5
 */

///////////////////////////////////////////////////////////////////////////
// 计算密集（CPU密集）一般配置CPU处理器个数+/-1个线程，
// 所谓计算密集型就是指系统大部分时间是在做程序正常的计算任务，
// 例如数字运算、赋值、分配内存、内存拷贝、循环、查找、排序等，这些处理都需要CPU来完成。
//
//IO密集
// 是指系统大部分时间在跟I/O交互，而这个时间线程不会占用CPU来处理，即在这个时间范围内，可以由其他线程来使用CPU，因而可以多配置一些线程。
//
//混合型
//混合型的话，是指两者都占有一定的时间。
///////////////////////////////////////////////////////////////////////////
public final class ThreadUtil {

    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int THREAD_MAX = 128;
    //主线程
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private static final RejectedExecutionHandler mRejectedHandler = new ThreadPoolExecutor.DiscardOldestPolicy();

    private static final ExecutorService UTIL_POOL = ThreadUtil.io();


    public static int getCoreCount(@ThreadPool.PoolType int type) {
        switch (type) {
            case ThreadPool.POOL_FIXED:
            case ThreadPool.POOL_CPU:
                return CPU_COUNT + 1;
            case ThreadPool.POOL_CACHED:
                return 0;
            case ThreadPool.POOL_IO:
                return (CPU_COUNT << 1) + 1;
            case ThreadPool.POOL_SINGLE:
            case ThreadPool.POOL_CUSTOM:
            case ThreadPool.POOL_SCHEDULED:
            default:
                return 1;
        }
    }

    public static int getMaxCount(@ThreadPool.PoolType int type) {
        switch (type) {
            case ThreadPool.POOL_FIXED:
                return CPU_COUNT + 1;
            case ThreadPool.POOL_CACHED:
                return THREAD_MAX;
            case ThreadPool.POOL_IO:
            case ThreadPool.POOL_CPU:
                return (CPU_COUNT << 1) + 1;
            case ThreadPool.POOL_SINGLE:
            case ThreadPool.POOL_CUSTOM:
            case ThreadPool.POOL_SCHEDULED:
            default:
                return 1;
        }

    }

    public static boolean isUI() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void runUI(final Runnable runnable) {
        if (isUI()) runnable.run();
        else HANDLER.post(runnable);
    }

    public static void runUiDelayed(final Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    public static void doAsync(@NonNull Runnable runnable) {
        UTIL_POOL.execute(runnable);
    }

    public static Future<?> submit(@NonNull Runnable runnable) {
        return UTIL_POOL.submit(runnable);
    }

    /**
     * 参数说明：
     * corePoolSize         -> 1
     * maximumPoolSize      -> 1
     * keepAliveTime        -> 0L
     * TimeUnit             -> TimeUnit.MILLISECONDS
     * WorkQueue            -> new LinkedBlockingQueue<Runnable>() 无解阻塞队列
     * <p>
     * 说明：创建只有一个线程的线程池，且线程的存活时间是无限的；当该线程正繁忙时，对于新任务会进入阻塞队列中(无界的阻塞队列)
     * <p>
     * 适用：一个任务一个任务执行的场景
     * 返回：ThreadPoolExecutor
     */
    public static ExecutorService single() {
        return single(new CustomThreadFactory(ThreadPool.POOL_SINGLE, Thread.NORM_PRIORITY), mRejectedHandler);
    }

    public static ExecutorService single(@IntRange(from = 1, to = 10) final int priority) {
        return single(new CustomThreadFactory(ThreadPool.POOL_SINGLE, priority), mRejectedHandler);
    }

    public static ExecutorService single(@NonNull final CustomThreadFactory factory, @NonNull final RejectedExecutionHandler handler) {
        final int core = getCoreCount(ThreadPool.POOL_SINGLE);
        final int max = getCoreCount(ThreadPool.POOL_SINGLE);
        return threadPool(core, max, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(THREAD_MAX), factory, handler);
    }

    /**
     * 参数说明：
     * corePoolSize         -> 自定义
     * maximumPoolSize      -> 自定义
     * keepAliveTime        -> 0L
     * TimeUnit             -> TimeUnit.MILLISECONDS
     * WorkQueue            -> new LinkedBlockingQueue<Runnable>() 无解阻塞队列
     * <p>
     * 说明：创建可容纳固定数量线程的池子，每隔线程的存活时间是无限的，当池子满了就不在添加线程了；
     * 如果池中的所有线程均在繁忙状态，对于新任务会进入阻塞队列中(无界的阻塞队列)
     * <p>
     * 适用：执行长期的任务，性能好很多
     * 返回：ThreadPoolExecutor
     */
    public static ExecutorService fixed() {
        final int core = getCoreCount(ThreadPool.POOL_FIXED);
        return threadPool(core, core, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(THREAD_MAX),
                new CustomThreadFactory(ThreadPool.POOL_FIXED, Thread.NORM_PRIORITY),
                mRejectedHandler
        );
    }

    public static ExecutorService fixed(final int core, @IntRange(from = 1, to = 10) final int priority, @Nullable final RejectedExecutionHandler rejectedHandler) {
        return threadPool(core, core, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(THREAD_MAX),
                new CustomThreadFactory(ThreadPool.POOL_FIXED, priority),
                null == rejectedHandler ? mRejectedHandler : rejectedHandler
        );
    }

    public static ExecutorService fixed(final int core, @Nullable final ThreadFactory factory, @Nullable RejectedExecutionHandler rejectedHandler) {
        return threadPool(core, core, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(THREAD_MAX),
                null == factory ? new CustomThreadFactory(ThreadPool.POOL_FIXED, Thread.NORM_PRIORITY) : factory,
                null == rejectedHandler ? mRejectedHandler : rejectedHandler
        );
    }

    /**
     * 参数说明：
     * nThreads             -> 0
     * maximumPoolSize      -> Integer.MAX_VALUE
     * keepAliveTime        -> 60L
     * TimeUnit             -> TimeUnit.SECONDS
     * WorkQueue            -> new SynchronousQueue<Runnable>()(同步队列)
     * <p>
     * 说明：当有新任务到来，则插入到SynchronousQueue中，由于SynchronousQueue是同步队列，因此会在池中寻找可用线程来执行，
     * 若有可以线程则执行，若没有可用线程则创建一个线程来执行该任务；若池中线程空闲时间超过指定大小，则该线程会被销毁。
     * <p>
     * 适用：执行很多短期异步的小程序或者负载较轻的服务器
     * 返回：ThreadPoolExecutor
     */
    public static ExecutorService cache() {
        final int core = getCoreCount(ThreadPool.POOL_CACHED);
        final int max = getMaxCount(ThreadPool.POOL_CACHED);
        return threadPool(core, max, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new CustomThreadFactory(ThreadPool.POOL_CACHED, Thread.NORM_PRIORITY),
                mRejectedHandler
        );
    }

    public static ExecutorService cache(int max, @IntRange(from = 1, to = 10) final int priority) {
        return threadPool(getCoreCount(ThreadPool.POOL_CACHED), max, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new CustomThreadFactory(ThreadPool.POOL_CACHED, priority),
                mRejectedHandler
        );
    }

    public static ExecutorService cache(final int max, @Nullable final ThreadFactory factory, @Nullable RejectedExecutionHandler rejectedHandler) {
        return threadPool(getCoreCount(ThreadPool.POOL_CACHED), max, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                null == factory ? new CustomThreadFactory(ThreadPool.POOL_CACHED, Thread.NORM_PRIORITY) : factory,
                null == rejectedHandler ? mRejectedHandler : rejectedHandler
        );
    }

    public static ExecutorService io() {
        return threadPool(getCoreCount(ThreadPool.POOL_IO), getMaxCount(ThreadPool.POOL_IO), 30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(THREAD_MAX),
                new CustomThreadFactory(ThreadPool.POOL_IO, Thread.NORM_PRIORITY),
                mRejectedHandler
        );
    }

    public static ExecutorService cup() {
        return threadPool(getCoreCount(ThreadPool.POOL_CPU), getMaxCount(ThreadPool.POOL_CPU), 30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(THREAD_MAX),
                new CustomThreadFactory(ThreadPool.POOL_CPU, Thread.NORM_PRIORITY),
                mRejectedHandler
        );
    }

    public static ExecutorService threadPool(@IntRange(from = 0, to = 4) final int core,
                                             @IntRange(from = 0, to = Integer.MAX_VALUE) final int max,
                                             final long keepAliveTime, @NonNull final TimeUnit unit,
                                             @NonNull BlockingQueue<Runnable> queue,
                                             @NonNull ThreadFactory factory,
                                             @NonNull RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(core, max, keepAliveTime, unit, queue, factory, handler);

    }

    private static final class CustomThreadFactory extends AtomicLong implements ThreadFactory {
        @ThreadPool.PoolType
        private final int mPoolType;
        private final int mPriority;
        private final boolean mDaemon;

        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        public CustomThreadFactory(@ThreadPool.PoolType int type, @IntRange(from = 1, to = 10) int priority) {
            this(type, priority, false);
        }

        public CustomThreadFactory(@ThreadPool.PoolType int type, @IntRange(from = 1, to = 10) int priority, boolean daemon) {
            this.mPoolType = type;
            this.mPriority = priority;
            this.mDaemon = daemon;
        }


        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread(r, threadName(getAndDecrement())) {
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
                case ThreadPool.POOL_SINGLE:
                    return "single-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case ThreadPool.POOL_FIXED:
                    return "fixed-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case ThreadPool.POOL_CACHED:
                    return "cache-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case ThreadPool.POOL_SCHEDULED:
                    return "scheduled-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case ThreadPool.POOL_IO:
                    return "io-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case ThreadPool.POOL_CPU:
                    return "cpu-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
                case ThreadPool.POOL_CUSTOM:
                default:
                    return "other-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-" + dec;
            }
        }
    }


}
