package com.norman.runtime;

import android.app.Application;
import android.content.Context;

public class AppRuntime {
    private static Application sApplication;
    private static boolean sBuildDebug;
    /** 全局debug开关. */
    public static boolean GLOBAL_DEBUG; // SUPPRESS CHECKSTYLE

    public static void init(Application application, boolean buildDebug) {
        sApplication = application;
        sBuildDebug = buildDebug;
        GLOBAL_DEBUG = buildDebug;
    }

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getAppContext() {
        return sApplication;
    }

    public static boolean isDebug() {
        return sBuildDebug;
    }

//    public static String getBaseUrl() {
//        return Constants.BASE_URL;
//    }
//    public static String getBaseSourceUrl() {
//        return Constants.BASE_SOURCE_URL;
//    }

}
