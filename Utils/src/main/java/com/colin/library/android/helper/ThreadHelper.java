package com.colin.library.android.helper;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.PoolType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
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
 * <a href="https://www.jianshu.com/p/a27416b7f01f">...</a>
 * 描述： 阻塞系数 = 阻塞时间 / 总的运行时间，如果任务有 50% 的时间处于阻塞状态，则阻塞系数为0.5
 */

///////////////////////////////////////////////////////////////////////////
// 线程池类型  IO流网络请求  CPU复杂运算操作
//
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
public final class ThreadHelper {
    public static final int THREAD_MAX = 128;
    public static final int THREAD_CPU_COUNT = Runtime.getRuntime().availableProcessors();

    //主线程
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private final ExecutorService POOL;

    private ThreadHelper() {
        POOL = new ThreadPoolExecutor(getCoreCount(PoolType.IO), getMaxCount(PoolType.IO), 30L, TimeUnit.SECONDS,
                                      new LinkedBlockingQueue<>(THREAD_MAX), new CustomThreadFactory(PoolType.IO, Thread.MAX_PRIORITY),
                                      getDefaultRejected());
    }

    private static final class Holder {
        private static final ThreadHelper instance = new ThreadHelper();
    }

    public static ThreadHelper getInstance() {
        return ThreadHelper.Holder.instance;
    }

    public boolean isMain() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public int getCoreCount(@PoolType int type) {
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

    public int getMaxCount(@PoolType int type) {
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

    public RejectedExecutionHandler getDefaultRejected() {
        return new ThreadPoolExecutor.DiscardOldestPolicy();
    }

    public void post(@NonNull final Runnable runnable) {
        if (isMain()) runnable.run();
        else HANDLER.post(runnable);
    }

    public void postDelayed(@NonNull final Runnable runnable, final long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    public void doAsync(@NonNull final Runnable runnable) {
        POOL.execute(runnable);
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
    @NonNull
    public ExecutorService single() {
        return single(new CustomThreadFactory(PoolType.SINGLE, Thread.NORM_PRIORITY), getDefaultRejected());
    }

    @NonNull
    public ExecutorService single(@IntRange(from = 1, to = 10) final int priority) {
        return single(new CustomThreadFactory(PoolType.SINGLE, priority), getDefaultRejected());
    }

    @NonNull
    public ExecutorService single(@NonNull final CustomThreadFactory factory, @NonNull final RejectedExecutionHandler handler) {
        return threadPool(getCoreCount(PoolType.SINGLE), getMaxCount(PoolType.SINGLE), 0L, TimeUnit.MILLISECONDS,
                          new LinkedBlockingQueue<>(THREAD_MAX), factory, handler);
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
    @NonNull
    public ExecutorService fixed() {
        final int core = getCoreCount(PoolType.FIXED);
        return threadPool(core, core, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(THREAD_MAX),
                          new CustomThreadFactory(PoolType.FIXED, Thread.NORM_PRIORITY), getDefaultRejected());
    }

    @NonNull
    public ExecutorService fixed(@IntRange(from = 1, to = 4) final int core, @IntRange(from = 1, to = 10) final int priority,
                                 @Nullable final RejectedExecutionHandler rejectedHandler) {
        return threadPool(core, core, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(THREAD_MAX),
                          new CustomThreadFactory(PoolType.FIXED, priority), null == rejectedHandler ? getDefaultRejected() : rejectedHandler);
    }

    @NonNull
    public ExecutorService fixed(@IntRange(from = 1, to = 4) final int core, @Nullable final ThreadFactory factory,
                                 @Nullable final RejectedExecutionHandler rejectedHandler) {
        return threadPool(core, core, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(THREAD_MAX),
                          null == factory ? new CustomThreadFactory(PoolType.FIXED, Thread.NORM_PRIORITY) : factory,
                          null == rejectedHandler ? getDefaultRejected() : rejectedHandler);
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
    @NonNull
    public ExecutorService cache() {
        return threadPool(getCoreCount(PoolType.CACHED), getMaxCount(PoolType.CACHED), 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
                          new CustomThreadFactory(PoolType.CACHED, Thread.NORM_PRIORITY), getDefaultRejected());
    }

    @NonNull
    public ExecutorService cache(@IntRange(from = 0, to = Integer.MAX_VALUE) int max, @IntRange(from = 1, to = 10) final int priority) {
        return threadPool(getCoreCount(PoolType.CACHED), max, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
                          new CustomThreadFactory(PoolType.CACHED, priority), getDefaultRejected());
    }

    @NonNull
    public ExecutorService cache(@IntRange(from = 0, to = Integer.MAX_VALUE) final int max, @Nullable final ThreadFactory factory,
                                 @Nullable final RejectedExecutionHandler rejectedHandler) {
        return threadPool(getCoreCount(PoolType.CACHED), max, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
                          null == factory ? new CustomThreadFactory(PoolType.CACHED, Thread.NORM_PRIORITY) : factory,
                          null == rejectedHandler ? getDefaultRejected() : rejectedHandler);
    }

    @NonNull
    public ExecutorService io() {
        return threadPool(getCoreCount(PoolType.IO), getMaxCount(PoolType.IO), 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(THREAD_MAX),
                          new CustomThreadFactory(PoolType.IO, Thread.NORM_PRIORITY), getDefaultRejected());
    }

    @NonNull
    public ExecutorService cup() {
        return threadPool(getCoreCount(PoolType.CPU), getMaxCount(PoolType.CPU), 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(THREAD_MAX),
                          new CustomThreadFactory(PoolType.CPU, Thread.NORM_PRIORITY), getDefaultRejected());
    }

    @NonNull
    public ExecutorService threadPool(@IntRange(from = 0, to = 4) final int core, @IntRange(from = 0, to = Integer.MAX_VALUE) final int max,
                                      @IntRange(from = 0, to = Integer.MAX_VALUE) final long keepAliveTime, @NonNull final TimeUnit unit,
                                      @NonNull final BlockingQueue<Runnable> queue, @NonNull final ThreadFactory factory,
                                      @NonNull final RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(core, max, keepAliveTime, unit, queue, factory, handler);
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

        public CustomThreadFactory(@PoolType int type, @IntRange(from = 1, to = 10) int priority, boolean daemon) {
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
