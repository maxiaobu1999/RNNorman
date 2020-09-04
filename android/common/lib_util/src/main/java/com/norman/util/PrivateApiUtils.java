package com.norman.util;

import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PrivateApiUtils {

    public static final int SDK_VERSION_P = 28;

    static {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= SDK_VERSION_P) {
            try {
                System.loadLibrary("private-p-master");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新添加一个static方法，将使用私有api的代码块迁移到新添加的方法中，在原来使用私有api代码块的位置改为调用这个新添加的方法
     */
    public static void usePrivateApi(){
        /**
         * 使用私有api的代码块
         */
    }


    /**
     * 获取
     * @return SystemProterties中qemu.hw.mainkeys方法
     */
    public static String getSystemProtertiesMethod(String key)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        Class c = Class.forName("android.os.SystemProperties");
        Method m = c.getDeclaredMethod("get", String.class);
        m.setAccessible(true);
        return (String) m.invoke(null, key);
    }


    /**
     *  获取系统的属性值
     * @param key property key
     * @param defaultValue default value.
     * @return
     */
    public static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取MIUI的渠道号
     * @param pkg
     * @return
     */
    public static String getMiuiChannel(String pkg) {
        try {
            Class<?> miui = Class.forName("miui.os.MiuiInit");
            Method method = miui.getMethod("getMiuiChannelPath", String.class);
            // 调用接口
            return (String) method.invoke(null, pkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
