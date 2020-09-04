package com.norman.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;


import com.norman.runtime.AppRuntime;

import java.lang.reflect.Method;

/** 公共类：UI 相关工具方法 */
public class UIUtils {
    /** DisplayMetrics 对象 */
    private static DisplayMetrics sDisplayMetrics;
    /** 不带alpha通道的颜色标准位数 */
    private static final int NO_ALPHA_STANDARD = 7;
    /** 带alpha通道的颜色标准位数 */
    private static final int WITH_ALPHA_STANDARD = 9;

    /** 标准状态栏高度 */
    private static final int STANDARD_STATUSBAR_HEIGHT = 50;
    /** 系统显示 */
    private static final DisplayMetrics DISPLAY_METRICS = AppRuntime.getAppContext()
            .getResources().getDisplayMetrics();
    /** 屏幕密度 */
    private static final float SCREEN_DENSITY = DISPLAY_METRICS.density;

    /**
     * 返回显示宽度
     *
     * @param context 内部使用框的Context，传入值被忽略
     * @return 宽度。DisplayMetrics初始化失败会返回0
     */
    public static int getDisplayWidth(Context context) {
        // 使用框的Context，防止插件调用接口时传入自己的Context
        initDisplayMetrics(AppRuntime.getAppContext());
        if (sDisplayMetrics != null) {
            return sDisplayMetrics.widthPixels;
        } else {
            return 0;
        }
    }

    /**
     * 得到显示高度
     *
     * @param context 内部使用框的Context，传入值被忽略
     * @return 高度。DisplayMetrics初始化失败会返回0
     */
    public static int getDisplayHeight(Context context) {
        // 使用框的Context，防止插件调用接口时传入自己的Context
        initDisplayMetrics(AppRuntime.getAppContext());
        if (sDisplayMetrics != null) {
            return sDisplayMetrics.heightPixels;
        } else {
            return 0;
        }
    }

    /**
     * 得到显示密度
     *
     * @param context 内部使用框的Context，传入值被忽略
     * @return 密度。DisplayMetrics初始化失败会返回0
     */
    public static float getDensity(Context context) {
        // 使用框的Context，防止插件调用接口时传入自己的Context
        initDisplayMetrics(AppRuntime.getAppContext());
        if (sDisplayMetrics != null) {
            return sDisplayMetrics.density;
        } else {
            return 0;
        }
    }

    /**
     * 得到DPI
     *
     * @param context 内部使用框的Context，传入值被忽略
     * @return DPI。DisplayMetrics初始化失败会返回0
     */
    public static int getDensityDpi(Context context) {
        // 使用框的Context，防止插件调用接口时传入自己的Context
        initDisplayMetrics(AppRuntime.getAppContext());
        if (sDisplayMetrics != null) {
            return sDisplayMetrics.densityDpi;
        } else {
            return 0;
        }
    }


    /**
     * 初始化DisplayMetrics
     *
     * @param context Context
     */
    private static void initDisplayMetrics(Context context) {
        if (null == sDisplayMetrics) {
            Context appContext = AppRuntime.getAppContext();
            if (null == appContext) {
                appContext = context;
            }
            if (null == appContext) {
                return;
            }
            sDisplayMetrics = appContext.getResources().getDisplayMetrics();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue dp 的单位
     * @return px(像素)的单位
     */
    public static int dp2px(float dpValue) {
        return dip2px(AppRuntime.getAppContext(), dpValue);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue dp 的单位
     * @return 浮点数的px(像素)的单位
     */
    public static float dp2pxf(float dpValue) {
        final float scale = getDensity(AppRuntime.getAppContext());
        return dpValue * scale;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param pxValue px(像素) 的单位
     * @return dp的单位
     */
    public static int px2dp(float pxValue) {
        return px2dip(AppRuntime.getAppContext(), pxValue);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文句柄
     * @param dpValue dp 的单位
     * @return px(像素)的单位
     */
    public static int dip2px(Context context, float dpValue) {
        // final float scale = context.getResources().getDisplayMetrics().density;
        final float scale = getDensity(context);
        return (int) (dpValue * scale);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context 上下文句柄
     * @param pxValue px(像素) 的单位
     * @return dp的单位
     */
    public static int px2dip(Context context, float pxValue) {
        // final float scale = context.getResources().getDisplayMetrics().density;
        final float scale = getDensity(context);
        return (int) (pxValue / scale);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param pxValue px(像素) 的单位
     * @return dp的单位, float类型
     */
    public static float px2dpFloat(float pxValue) {
        final float scale = getDensity(AppRuntime.getAppContext());
        return pxValue / scale;
    }

    /**
     * 计算一个TextView的高度
     *
     * @param view textview
     * @return 该textView的实际高度
     */
    public static int getTextViewHeight(TextView view) {
        int height = 0;
        if (null == view) {
            return height;
        }
        Paint paint = new Paint();
        paint.setTextSize(view.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        if (!TextUtils.isEmpty(view.getText())) {
            height = (int) (Math.ceil(fm.descent - fm.ascent) + 2);
        }
        return height;
    }

    /**
     * 计算一个TextView的宽度
     *
     * @param view textview
     * @return int 该textview的实际宽度
     */
    public static int getTextViewWidth(TextView view) {
        int width = 0;
        if (view == null) {
            return width;
        }
        Paint paint = new Paint();
        paint.setTextSize(view.getTextSize());
        if (!TextUtils.isEmpty(view.getText())) {
            width = (int) paint.measureText(view.getText().toString());
        }
        return width;
    }

    /**
     * 检查颜色数据是否合法
     *
     * @param color 颜色
     * @return 是否合法
     */
    public static boolean isColorValid(Object color) {
        if (color instanceof String) {
            String checkColor = String.valueOf(color);
            if (TextUtils.isEmpty(checkColor)) {
                return false;
            }
            return checkColor.startsWith("#")
                    && (checkColor.length() == NO_ALPHA_STANDARD || checkColor.length() == WITH_ALPHA_STANDARD);
        }
        if (color instanceof Integer) {
            return true;
        }
        return false;
    }

    /**
     * 获取状态栏高度
     *
     * @return int 状态栏高度
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = AppRuntime.getAppContext().getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            try {
                result = AppRuntime.getAppContext().getResources().getDimensionPixelSize(resourceId);
            } catch (Exception e) {
                result = 0;
            }
        }
        if (result == 0) {
            result = (int) (STANDARD_STATUSBAR_HEIGHT / 2 * SCREEN_DENSITY);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     *
     * @return int 导航栏高度
     */
    public static int getNavigationBarHeight() {
        int height = 0;
        boolean hasMenuKey = ViewConfiguration.get(AppRuntime.getAppContext()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        // 没有menu按键且没有back按键
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = AppRuntime.getAppContext().getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            // 获取导航栏的高度
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    /**
     * 竖屏状态
     *
     * @return true
     */
    public static boolean isScreenPortrait() {
        // 获取设置的配置信息
        Configuration cf = AppRuntime.getAppContext().getResources().getConfiguration();
        return cf.orientation == cf.ORIENTATION_PORTRAIT;
    }

    /**
     * 横屏状态
     *
     * @return true
     */
    public static boolean isScreenLand() {
        // 获取设置的配置信息
        Configuration cf = AppRuntime.getAppContext().getResources().getConfiguration();
        return cf.orientation == cf.ORIENTATION_LANDSCAPE;
    }

    /**
     * 判断当前的屏幕密度是否比原始的屏幕密度大。
     *
     * @param activity 当前Activity
     * @return 当前的屏幕密度是否比原始的屏幕密度大，返回true，否则返回false
     */
    public static boolean isDensityTooLarge(Activity activity) {
        if (APIUtils.hasNougat() && activity != null) {
            int originDensityDip = 0;
            try {
                Object iWindowManager = new Object();
                Method windowManagerGlobalMethod = Class.forName("android.view.WindowManagerGlobal")
                        .getMethod("getWindowManagerService");
                iWindowManager = windowManagerGlobalMethod.invoke(iWindowManager);
                Method iWindowManagerMethod = Class.forName("android.view.IWindowManager")
                        .getMethod("getInitialDisplayDensity", int.class);
                originDensityDip = (int) iWindowManagerMethod.invoke(iWindowManager, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int screenOriginDensityDip = originDensityDip;
            if (screenOriginDensityDip <= 0 || activity.isInMultiWindowMode()) {
                return false;
            }

            float screenOriginDensity = screenOriginDensityDip / 160f;
            DisplayMetrics metric = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            float currentDensity = metric.density;
            return currentDensity > screenOriginDensity;
        }
        return false;
    }
}
