package com.norman.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.norman.runtime.AppRuntime;

import org.reactivestreams.Publisher;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.schedulers.Schedulers;


/**
 * 提供统一的Executor
 * 目前提供三种不同实现的Executor：
 * 1，getIoExecutor()：涉及IO操作请使用此Executor，该Executor使用RxJava的Schedulers.io()实现，不限制线程数量。
 * 所有提交到此Executor的任务都会立即分配一个线程执行
 * 2，getComputationExecutor()：涉及需要cpu操作的请使用此Executor，该Executor使用RxJava的Schedulers.computation()实现.
 * 线程池数量为cpu数。提交到此Executor的任务会被加入到某一个线程的队列里等待执行
 * 3, getSerialExecutor()：需要串行执行的后台任务请使用该Executor。所有提交到此Executor的任务会被依次串行执行
 * <p>
 * 另外，提供直接pust任务到制定Executor的接口
 */
public final class ExecutorUtils {

    /** TAG **/
    private static final String TAG = ExecutorUtils.class.getSimpleName();
    /** DEBUG **/
    private static final boolean DEBUG = AppRuntime.isDebug();

    /** 涉及IO操作请使用此Executor，该Executor使用RxJava的Schedulers.io()实现，不限制线程数量。**/
    private static RxExecutor IO_EXECUTOR;
    /** 涉及需要cpu操作的请使用此Executor，该Executor使用RxJava的Schedulers.computation()实现. **/
    private static RxExecutor COMPUTATION_EXECUTOR;
    /** 需要串行执行的后台任务请使用该Executor。所有提交到此Executor的任务会被依次串行执行 **/
    private static RxExecutor SERIAL_EXECUTOR;

    /** EXECUTE ACTION **/
    private static final Consumer<Pair<Runnable, String>> EXECUTE_ACTION = new Consumer<Pair<Runnable, String>>() {

        @Override
        public void accept(Pair<Runnable, String> runnableStringPair) {
            String preName = Thread.currentThread().getName();
            Thread.currentThread().setName(preName + "-" + runnableStringPair.mSecond);
            try {
                long before = 0;
                if (DEBUG) {
                    before = System.currentTimeMillis();
                }
                runnableStringPair.mFirst.run();
                if (DEBUG) {
                    Log.d(TAG, "Task [" + runnableStringPair.mSecond + "] caused "
                            + (System.currentTimeMillis() - before) + "ms");
                }
            } catch (Throwable t) {
                if (DEBUG) {
                    Log.w(TAG, "Task [" + runnableStringPair.mSecond + "] fail!", t);
                    throw t;
                }
            }
            Thread.currentThread().setName(preName);
        }
    };

    /**
     * 构造函数
     */
    private ExecutorUtils() {
    }

    /**
     * 获取IOExecutor
     * @return IOExecutor
     */
    public static IUtilExecutor getIoExecutor() {
        if (IO_EXECUTOR == null) {
            synchronized (ExecutorUtils.class) {
                if (IO_EXECUTOR == null) {
                    IO_EXECUTOR = new RxExecutor(CustomPublishProcessor.create());
                    IO_EXECUTOR.onBackpressureBuffer()
                            .flatMap(new Function<Pair<Runnable, String>, Publisher<?>>() {
                                @Override
                                public Publisher<?> apply(Pair<Runnable, String> runnableStringPair) throws Exception {
                                    return Flowable.just(runnableStringPair)
                                            .observeOn(Schedulers.io())
                                            .doOnNext(EXECUTE_ACTION);
                                }
                            })
                            .retry().subscribe();
                }
            }
        }
        return IO_EXECUTOR;
    }

    /**
     * 获取ComputationExecutor
     * @return ComputationExecutor
     */
    public static IUtilExecutor getComputationExecutor() {
        if (COMPUTATION_EXECUTOR == null) {
            synchronized (ExecutorUtils.class) {
                if (COMPUTATION_EXECUTOR == null) {
                    COMPUTATION_EXECUTOR = new RxExecutor(CustomPublishProcessor.create());
                    COMPUTATION_EXECUTOR.onBackpressureBuffer()
                            .flatMap(new Function<Pair<Runnable, String>, Publisher<?>>() {
                                @Override
                                public Publisher<?> apply(Pair<Runnable, String> runnableStringPair) throws Exception {
                                    return Flowable.just(runnableStringPair)
                                            .observeOn(Schedulers.computation())
                                            .doOnNext(EXECUTE_ACTION);
                                }
                            })
                            .retry().subscribe();
                }
            }
        }
        return COMPUTATION_EXECUTOR;
    }

    /**
     * 获取SerialExecutor
     * 串行线程池
     * @return SerialExecutor
     */
    public static IUtilExecutor getSerialExecutor() {
        if (SERIAL_EXECUTOR == null) {
            synchronized (ExecutorUtils.class) {
                if (SERIAL_EXECUTOR == null) {
                    SERIAL_EXECUTOR = new RxExecutor(CustomPublishProcessor.create());
                    SERIAL_EXECUTOR.onBackpressureBuffer()
                            .observeOn(Schedulers.io())
                            .doOnNext(EXECUTE_ACTION)
                            .retry().subscribe();
                }
            }
        }
        return SERIAL_EXECUTOR;
    }

    /**
     * 用于涉及IO操作的需要在非UI线程执行的一次性任务
     *
     * @param r
     * @param name 任务名
     */

    public static void postOnIO(@NonNull final Runnable r, @NonNull final String name) {
        getIoExecutor().execute(r, name);
    }

    /**
     * 用于涉及cpu耗时操作的需要在非UI线程执行的一次性任务
     *
     * @param r
     * @param name 任务名
     */
    public static void postOnComputation(@NonNull final Runnable r, @NonNull final String name) {
        getComputationExecutor().execute(r, name);
    }

    /**
     * 用于需要在非UI线程串行执行的一次性任务
     *
     * @param r
     * @param name 任务名
     */

    public static void postOnSerial(@NonNull final Runnable r, @NonNull final String name) {
        getSerialExecutor().execute(r, name);
    }

    /**
     * 提交延迟任务在后台执行, 注意使用此接口一定要在不需要的时候对返回的Subscription做unsubscribe
     *
     * @param r
     * @param name
     * @param delay
     * @param unit
     *
     * @return Subscription对象, 可用于取消未完成的任务
     */
    public static Disposable delayPostOnIO(@NonNull final Runnable r, @NonNull final String name,
                                           @NonNull long delay,
                                           @NonNull TimeUnit unit) {
        return Single.just(Pair.create(r, getStandardThreadName(name))).delay(delay, unit)
                .doOnSuccess(new Consumer<Pair<Runnable, String>>() {
                    @Override
                    public void accept(Pair<Runnable, String> runnableStringPair) {
                        getIoExecutor().execute(runnableStringPair.mFirst, runnableStringPair.mSecond);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (DEBUG) {
                            Log.wtf(TAG, "delay task [" + name + "] fail!", throwable);
                        }
                    }
                })
                .subscribe();
    }

    /**
     * 提交延迟任务在后台执行, 注意使用此接口一定要在不需要的时候对返回的Subscription做unsubscribe
     *
     * @param r
     * @param name
     * @param delay
     * @param unit
     *
     * @return Subscription对象, 可用于取消未完成的任务
     */
    public static Disposable delayPostOnComputation(@NonNull final Runnable r, @NonNull final String name,
                                                      @NonNull long delay,
                                                      @NonNull TimeUnit unit) {
        return Single.just(Pair.create(r, getStandardThreadName(name))).delay(delay, unit)
                .doOnSuccess(new Consumer<Pair<Runnable, String>>() {
                    @Override
                    public void accept(Pair<Runnable, String> runnableStringPair) {
                        getComputationExecutor().execute(runnableStringPair.mFirst, runnableStringPair.mSecond);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (DEBUG) {
                            Log.wtf(TAG, "delay task [" + name + "] fail!", throwable);
                        }
                    }
                })
                .subscribe();
    }

    /**
     * 提交延迟任务在后台执行, 注意使用此接口一定要在不需要的时候对返回的Subscription做unsubscribe
     *
     * @param r
     * @param name
     * @param delay
     * @param unit
     *
     * @return Subscription对象, 可用于取消未完成的任务
     */
    public static Disposable delayPostOnSerial(@NonNull final Runnable r, @NonNull final String name,
                                                 @NonNull long delay,
                                                 @NonNull TimeUnit unit) {
        return Single.just(Pair.create(r, getStandardThreadName(name))).delay(delay, unit)
                .doOnSuccess(new Consumer<Pair<Runnable, String>>() {
                    @Override
                    public void accept(Pair<Runnable, String> runnableStringPair) {
                        getSerialExecutor().execute(runnableStringPair.mFirst, runnableStringPair.mSecond);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (DEBUG) {
                            Log.wtf(TAG, "delay task [" + name + "] fail!", throwable);
                        }
                    }
                })
                .subscribe();
    }

    /**
     * 获取标准线程名
     *
     * @param name 线程名
     *
     * @return 处理过的线程名
     */
    public static String getStandardThreadName(String name) {
        String newName = null;
        if (name != null) {
            String prefix = TAG + "_";
            if (!name.startsWith(prefix)) {
                newName = prefix + name;
            } else {
                newName = name;
            }
        }

        if (newName == null) {
            newName = TAG;
        }

        if (newName.length() > 256) {
            newName = newName.substring(0, 255);
        }

        return newName;
    }

    /**
     * 增加一个提交任务时加上taskName的方法的Executor
     */
    public interface IUtilExecutor extends Executor {
        void execute(@NonNull Runnable command, @NonNull String taskName);
    }

    /**
     * 封装Executor interface的Subject
     */
    private static class RxExecutor extends CustomPublishProcessor<Pair<Runnable, String>>
            implements IUtilExecutor {

        public RxExecutor(FlowableProcessor actual) {
            super();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            execute(command, "");
        }

        @Override
        public void execute(@NonNull Runnable command, @NonNull String taskName) {
            onNext(Pair.create(command, getStandardThreadName(taskName)));
        }

    }
}
