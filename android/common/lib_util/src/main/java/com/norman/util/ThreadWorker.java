package com.norman.util;

import android.os.Looper;
import android.os.Process;

/**
 * The thread worker. This class will starts a thread with a looper, and it is alive until you call
 * {@link #quit()} method.
 */
public final class ThreadWorker implements Runnable {
    /**
     * The lock object.
     */
    private final Object mLockObj = new Object();

    /**
     * The looper of the runnable.
     */
    private Looper mLooper = null;

    /**
     * Creates a worker thread with the given name. The thread then runs a {@link android.os.Looper}
     *
     * @param name A name for the new thread
     */
    public ThreadWorker(String name) {
        // Start a thread.
        Thread t = new Thread(null, this, name);
        t.setPriority(Thread.MIN_PRIORITY);
        // The constructor starts a thread. This is likely to be wrong if the class is ever extended/subclassed,
        // since the thread will be started before the subclass constructor is started.
        t.start();

        synchronized (mLockObj) {
            // Wait for until the loop succeeds to prepare.
            while (null == mLooper) {
                try {
                    mLockObj.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the looper of the thread.
     *
     * @return Looper object.
     */
    public Looper getLooper() {
        return mLooper;
    }

    /**
     * Blocks the current thread ({@link Thread#currentThread()}) until the receiver finishes its
     * execution and dies.
     */
    public void join() {
        Looper looper = getLooper();
        if (null != looper) {
            try {
                Thread thread = looper.getThread();
                if (null != thread) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The run method of Runnable.
     */
    @Override
    public void run() {
        synchronized (mLockObj) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            // Initialize the looper of the current thread.
            Looper.prepare();
            // Get the looper.
            mLooper = Looper.myLooper();
            // Notification all waiting object.
            mLockObj.notifyAll();
        }

        // Begin the loop cycle.
        Looper.loop();
    }

    /**
     * Quit the message queue.
     */
    public void quit() {
        mLooper.quit();
    }

    /**
     * Pause the thread to run.
     */
    public void pause() {
        Thread thread = mLooper.getThread();
        if (null != thread) {
            try {
                synchronized (thread) {
                    thread.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Restart the thread to run
     */
    public void restart() {
        Thread thread = mLooper.getThread();
        if (null != thread) {
            try {
                synchronized (thread) {
                    thread.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
