
package com.norman.util;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import com.norman.runtime.AppRuntime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link AsyncTask} 类的辅助工具类，方便直接执行 {@link Runnable} 任务。<br>
 * 并且支持延时  {@link Runnable} 任务，内部通过 {@link Handler#postDelayed(Runnable, long)} 实现，
 * <p>
 * 为了隐藏这些细节，这个类的 {@link #execute(Runnable, String)} 函数也是按顺序执行，只不过和android版本没有关系。<br>
 * 如果想在线程池执行，请调用 {@link #executeOnThreadPool(Runnable, String)} 相关函数。
 * <p>
 * 原理就是配置了线程池的AsyncTask
 */
public final class AsyncTaskAssistant {
    /** debug */
    @SuppressWarnings("PointlessBooleanExpression")
    private static final boolean DEBUG = true & AppRuntime.GLOBAL_DEBUG;
    private static final String TAG = "AsyncTaskAssistant";
    /** 这个只是用来做延迟的，这里直接创建实例 */
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    /** CPU数量 */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /** 核心线程数 */
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    /** 最大线程数， cpu数减一，避免抢占主线程cpu */
    private static final int MAXIMUM_POOL_SIZE = Math.max(2, CPU_COUNT - 1);
    /** 空闲线程销毁时间 */
    private static final int KEEP_ALIVE_SECONDS = 30;
    /** 线程创建工厂 */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    };
    /** 阻塞队列 */
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<>();
    /** 线程池 */
    private static final Executor THREAD_POOL_EXECUTOR;

    static {
        if (DEBUG) {
            Log.d(TAG, "core pool size: " + CORE_POOL_SIZE + " max size: " + MAXIMUM_POOL_SIZE);
        }
        // 这里增加core thread size，启动时间明显下降
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                MAXIMUM_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);// 队列空闲，核心线程会销毁。tip：JDK1.5队列不满不创建新线程
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    /** 内部使用参数类。用作执行任务的参数。 */
    private static class Task {
        /** 所具体执行的任务。 */
        Runnable runnable;
        /** 线程名字 */
        String name;
    }

    /**
     * 用来实际执行任务的 {@link AsyncTask}.
     */
    private static class WorkerAsyncTask extends AsyncTask<Task, Object, Object> {

        @Override
        protected Object doInBackground(Task... params) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                if (params[0] != null && params[0].runnable != null) {
                    String name;
                    if (!TextUtils.isEmpty(params[0].name)) {
                        name = params[0].name;
                    } else {
                        name = "noname";
                    }
                    Thread.currentThread().setName(name);
                    if (DEBUG) {
                        Log.d(TAG, "start to run task " + name);
                    }

                    Runnable task = params[0].runnable;
                    task.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /** Utility class use a private constructor. */
    private AsyncTaskAssistant() {
    }

    /**
     * 一个在 {@link AsyncTask} 中执行 {@link Runnable} 任务的快捷方式。
     * 任务按顺序执行，一个任务执行完后，才执行下一个任务。
     *
     * @param runnable 要执行的任务
     */
    public static void execute(Runnable runnable, String name) {
        Task task = new Task();
        task.runnable = runnable;
        task.name = name;
        new WorkerAsyncTask().execute(task);
    }

    /**
     * 延时执行{@link Runnable} 任务.
     *
     * @param runnable    要执行的任务。
     * @param delayTimeMS 需要延时的时间，单位毫秒。
     */
    public static void execute(final Runnable runnable, final String name, long delayTimeMS) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                execute(runnable, name);
            }
        };
        sHandler.postDelayed(r, delayTimeMS);
    }

    /**
     * 一个在 {@link AsyncTask} 中执行 {@link Runnable} 任务的快捷方式。
     * 执行顺序无法保证，在线程池执行。
     *
     * @param runnable 要执行的任务
     * @param name 任务名
     * @see AsyncTask#executeOnExecutor(Executor, Object...)
     */
    @SuppressLint("NewApi")
    public static void executeOnThreadPool(Runnable runnable, String name) {
        Task task = new Task();
        task.runnable = runnable;
        task.name = name;
        new WorkerAsyncTask().executeOnExecutor(THREAD_POOL_EXECUTOR, task);
    }

    /**
     * 一个延时在 {@link AsyncTask} 中执行 {@link Runnable} 任务的快捷方式。
     * 执行顺序无法保证，在线程池执行。
     *
     * @param runnable    要执行的任务
     * @param delayTimeMS 需要延时的时间，单位毫秒。
     * @see AsyncTask#executeOnExecutor(Executor, Object...)
     */
    @SuppressWarnings("unused")
    public static void executeOnThreadPool(final Runnable runnable, final String name, long delayTimeMS) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                executeOnThreadPool(runnable, name);
            }
        };
        sHandler.postDelayed(r, delayTimeMS);
    }

}
