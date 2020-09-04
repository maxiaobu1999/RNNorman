
package com.norman.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LongSparseArray;

import androidx.annotation.DrawableRes;

import com.norman.runtime.AppRuntime;


/**
 * This class caches preload drawables in Application's default LoadedAPK.
 * @since 17-7-28 下午7:26
 */
public class PreloadUIResUtil {

    /**
     * tag for preload
     */
    public static final String TAG = "PreloadUIResUtil";
    /**
     * preload drawable resources cache.
     */
    private static final LongSparseArray<Drawable.ConstantState> mPreloadedDrawableCS = new LongSparseArray<>(30);
    /**
     * debug swticher
     */
    private static boolean DEBUG = true & AppRuntime.GLOBAL_DEBUG;

    /** Lock object used to protect access to caches and configuration. */
    private static final Object mAccessLock = new Object();

    /**
     * cache the drawable against the id. Don't update the drawable if it exists.
     *
     * @param id resource id of the drawable.
     */
    public static void preloadDrawable(@DrawableRes int id, String tag) {
        preloadDrawable(id);
        if (DEBUG) {
            Log.d(TAG, tag + " endtime = " + System.currentTimeMillis());
        }
    }

    /**
     * cache the drawable against the id. Don't update the drawable if it exists.
     *
     * @param id resource id of the drawable.
     */
    public static void preloadDrawable(@DrawableRes int id) {
        preloadDrawable(id, false);
    }

    /**
     * cache the drawable against the id.
     *
     * @param id     resource id of the drawable.
     * @param update True is to update the instance if the cache already got one.
     */
    public static void preloadDrawable(@DrawableRes int id, boolean update) {
        try {
            if (update) {
                Drawable drawable = AppRuntime.getAppContext().getResources().getDrawable(id);
                if (drawable != null) {
                    synchronized (mAccessLock) {
                        Drawable.ConstantState cs = drawable.getConstantState();
                        if (cs != null) {
                            mPreloadedDrawableCS.put(id, cs);
                        }
                    }
                }
            } else {
                Drawable.ConstantState drawableConState = mPreloadedDrawableCS.get(id);
                if (drawableConState == null) {
                    Drawable drawable = AppRuntime.getAppContext().getResources().getDrawable(id);
                    if (drawable != null) {
                        synchronized (mAccessLock) {
                            Drawable.ConstantState cs = drawable.getConstantState();
                            if (cs != null) {
                                mPreloadedDrawableCS.put(id, cs);
                            }
                        }
                    }
                }
            }
        } catch (Resources.NotFoundException nfe) {
            nfe.printStackTrace();
        }
    }

    /**
     * obtain preloaded drawable.
     *
     * @param id resource id of the drawable
     * @return a drawable or null
     */
    public static Drawable getPreloadedDrawable(@DrawableRes int id) {
        Drawable.ConstantState constantState = mPreloadedDrawableCS.get(id);
        if (constantState != null) {
            Resources res = AppRuntime.getAppContext().getResources();
            return constantState.newDrawable(res);
        }
        return null;
    }

    /**
     * clear drawable cache
     */
    public static void cleanPreloadedDrawable() {
        synchronized (mAccessLock) {
            mPreloadedDrawableCS.clear();
        }
    }
}
