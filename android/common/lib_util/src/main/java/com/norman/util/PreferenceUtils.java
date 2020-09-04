

package com.norman.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.norman.runtime.AppRuntime;


/**
 * 这个类提供一组访问Shared Preference的基础接口
 * 主要是优化接口读写性能
 *
 * @since 2014-10-30
 */
public final class PreferenceUtils {

    /**
     * preference对象
     **/
    private static SharedPreferences mPreference = null;
    /**
     * custom preference对象
     **/
    private static SharedPreferences mCustomPreference = null;
    /**
     * custom preference对象
     **/
    private static String mCustomPreferenceId = null;

    /**
     * 获取preference对象
     *
     * @return preference对象
     */
    private static SharedPreferences getPreference() {
        if (mPreference == null) {
            mPreference = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        }
        return mPreference;
    }

    /**
     * 构造方法
     */
    private PreferenceUtils() {
    }

    /**
     * 获得string
     *
     * @param key      KEY
     * @param defValue 默认值
     * @return String
     */
    public static String getString(String key, String defValue) {
        return getPreference().getString(key, defValue);
    }

    /**
     * 获得int
     *
     * @param key      KEY
     * @param defValue 默认值
     * @return String
     */
    public static int getInt(String key, int defValue) {
        return getPreference().getInt(key, defValue);
    }

    /**
     * 获得long
     *
     * @param key      KEY
     * @param defValue 默认值
     * @return String
     */
    public static long getLong(String key, long defValue) {
        return getPreference().getLong(key, defValue);
    }

    /**
     * 获得float
     *
     * @param key      KEY
     * @param defValue 默认值
     * @return String
     */
    public static float getFloat(String key, float defValue) {
        return getPreference().getFloat(key, defValue);
    }

    /**
     * 获得server下发的bool开关值.
     *
     * @param key      Key
     * @param defValue 默认值
     * @return 无对应key，则返回false
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return getPreference().getBoolean(key, defValue);
    }

    /**
     * preference删除指定KEY
     *
     * @param key key
     */
    public static void removeKey(String key) {
        if (getPreference().contains(key)) {
            Editor editor = getPreference().edit();
            editor.remove(key);
            editor.apply();
        }
    }

    /**
     * preference是否包含指定KEY
     *
     * @param key key
     * @return 是否包含
     */
    public static boolean containsKey(String key) {
        return getPreference().contains(key);
    }

    /**
     * 设置BOOL值
     *
     * @param key   Key
     * @param value Value
     */
    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 设置String值.
     *
     * @param key   key
     * @param value value
     */
    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 设置String值.
     *
     * @param key   key
     * @param value value
     */
    public static void setStringCommit(String key, String value) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 设置int值
     *
     * @param key   key
     * @param value value
     */
    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 设置long值
     *
     * @param key   key
     * @param value value
     */
    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 设置float值
     *
     * @param key   key
     * @param value value
     */
    public static void setFloat(String key, float value) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     *
     * @param listener The callback that will run.
     * @see SharedPreferences#unregisterOnSharedPreferenceChangeListener
     */
    public static void registerOnChangeListener(OnSharedPreferenceChangeListener listener) {
        getPreference().registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @see SharedPreferences#registerOnSharedPreferenceChangeListener
     */
    public static void unregisterOnChangeListener(OnSharedPreferenceChangeListener listener) {
        getPreference().unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 得到Application的Context
     *
     * @return context
     */
    private static Context getAppContext() {
        return AppRuntime.getAppContext();
    }

    /**
     * 获取一个CustomPreference (如果取框的默认preference，不建议使用这个方法)
     *
     * @param preferenceId preference id
     */
    public static SharedPreferences getCustomPreference(String preferenceId) {
        if (TextUtils.isEmpty(preferenceId)) {
            // 如果id为空，返回框的默认preference
            mCustomPreference = getPreference();
            mCustomPreferenceId = null;
        } else {
            if (mCustomPreference == null
                    || !TextUtils.equals(mCustomPreferenceId, preferenceId)) {
                mCustomPreference = getAppContext().getSharedPreferences(
                        preferenceId, Context.MODE_PRIVATE);
                mCustomPreferenceId = preferenceId;
            }
        }
        return mCustomPreference;
    }
}
