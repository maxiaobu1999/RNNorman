package com.norman.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

/**
 * 在 UI 线程中执行任务的工具类
 * @since 2018-06-06
 */
public class UiThreadUtil {

    /** 主线程的Handler */
    private static volatile Handler sMainHandler;

    /**
     * 判断是否在 UI 线程中
     *
     * @return true/false
     */
    public static boolean isOnUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 在 UI 线程中执行
     */
    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            getMainHandler().post(action);
        } else {
            action.run();
        }
    }
    
    /**
     * Runs the specified action on the UI thread. If the current thread is the UI thread, then the action is executed
     * immediately.If the current thread is not the UI thread, the action is posted to the event queue of the UI thread.
     * <p>
     * <p>
     * 该功能与{@link Activity#runOnUiThread(Runnable)}一样, put runnable at first queue
     * </p>
     *
     * @param action the action to run on the UI thread
     */
    public static void runOnUiThreadAtFrontOfQueue(Runnable action) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Handler handler = getMainHandler();
            handler.postAtFrontOfQueue(action);
        } else {
            action.run();
        }
    }

    /**
     * Runs the specified action on the UI thread. If the current thread is the UI thread, then the action is executed
     * immediately.If the current thread is not the UI thread, the action is posted to the event queue of the UI thread.
     *
     * <p>
     * 该功能与{@link Activity#runOnUiThread(Runnable)}一样
     * </p>
     *
     * @param action the action to run on the UI thread
     * @param delayMillis 延时
     */
    public static void runOnUiThread(Runnable action, long delayMillis) {
        if (delayMillis > 0) {
            Handler handler = getMainHandler();
            handler.postDelayed(action, delayMillis);
        } else {
            runOnUiThread(action);
        }
    }

    /**
     * 得到关联到主线程的Handler
     *
     * <p>注意：这个Handler通常只是用来执行post操作。</p>
     *
     * @return handler
     */
    public static Handler getMainHandler() {
        if (null == sMainHandler) {
            synchronized (UiThreadUtil.class) {
                if (null == sMainHandler) {
                    sMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }

        return sMainHandler;
    }
}
