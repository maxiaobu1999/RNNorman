package com.norman.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.norman.runtime.AppRuntime;

import java.util.Map;
import java.util.Set;

/**
 * Sp相当访问的代理，每个文件名需自己实现一个单例继承此类来访问，可以参考DefaultSharedPrefsWrapper
 *
 */
public class SharedPrefsWrapper implements SharedPreferences {
    private static final int MAX_STRING_LENGTH = 256;
    private static final boolean DEBUG = AppRuntime.GLOBAL_DEBUG;
    private SharedPreferences mSp;

    /**
     * <p><strong>构造方法只允许在每sp文件的单例中调用</strong></p>
     * 当文件名为empty或"default"时，获取app默认的sp，否则获取指定文件名sp
     *
     * @param fileName 文件名，当为空或"default"时获取默认的sp
     */
    public SharedPrefsWrapper(String fileName) {
        if (TextUtils.isEmpty(fileName) || "default".equals(fileName)) {
            mSp = PreferenceManager.getDefaultSharedPreferences(AppRuntime.getAppContext());
        } else {
            mSp = AppRuntime.getAppContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
    }

    @Override
    public Map<String, ?> getAll() {
        return mSp.getAll();
    }

    @Override
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return mSp.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mSp.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mSp.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return mSp.contains(key);
    }

    @Override
    public Editor edit() {
        return mSp.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSp.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        mSp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putString(String key, String value) {
        verifyLength(key, value);
        mSp.edit().putString(key, value).apply();

    }

    public void putStringSet(String key, Set<String> values) {
        verifyAllLength(key, values);
        mSp.edit().putStringSet(key, values).apply();
    }

    public void putInt(String key, int value) {
        mSp.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        mSp.edit().putLong(key, value).apply();
    }

    public void putFloat(String key, float value) {
        mSp.edit().putFloat(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        mSp.edit().putBoolean(key, value).apply();
    }

    public void remove(String key) {
        mSp.edit().remove(key).apply();
    }

    private void verifyLength(String key, String value) {
        if (value == null || value.length() <= MAX_STRING_LENGTH) {
            return;
        }
        if (DEBUG) {
            throw new IllegalArgumentException(String.format("the value of %s is %d, over the limit of %d!", key, value
                    .length(), MAX_STRING_LENGTH));
        }
    }

    private void verifyAllLength(String key, Set<String> values) {
        if (values == null) {
            return;
        }
        for (String item : values) {
            verifyLength(key, item);
        }
    }
}
