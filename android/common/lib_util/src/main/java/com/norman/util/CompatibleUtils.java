package com.norman.util;

import android.annotation.SuppressLint;
import android.content.Context;

import java.lang.reflect.Method;

/** 兼容相关工具类 */
public class CompatibleUtils {
    /**
     * 是否支持全屏，特殊情况下不支持全屏
     *
     * @return true:支持全屏 false:不支持全屏
     */
    @SuppressWarnings({"SpellCheckingInspection", "RedundantIfStatement"})
    public static boolean supportFullscreen(Context context) {
        if ("Xiaomi".equals(android.os.Build.BRAND)
                && "MIX".equals(android.os.Build.MODEL)) { // 小米MIX手机不支持全屏
            return false;
        } else if (isHisenseNotchScreen()) { // 海信刘海屏手机不支持全屏
            return false;
        } else if (context.getPackageManager() != null && context.getPackageManager()
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism")) {
            return false; // OPPO异形屏手机不支持全屏
        }
        return true;
    }


    /**
     * 是否为刘海屏手机
     *
     * @return true为刘海屏，otherwise not
     */
    public static boolean isHisenseNotchScreen() {
        int i = 0;
        try {
            @SuppressLint("PrivateApi")
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("getInt", String.class, int.class);
            m.setAccessible(true);
            i = (int) m.invoke(c, "ro.hmct.notch_height", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i > 0;
    }
}
