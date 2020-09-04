package com.norman.util;

import android.util.Log;

import com.norman.runtime.AppRuntime;

import java.lang.reflect.Method;

/**
 * 反射方法工具类
 */
public final class ReflectionUtils {

    /** DEBUG flag */
    private static final boolean DEBUG = AppRuntime.GLOBAL_DEBUG;
    /** DEBUG TAG */
    private static final String TAG = "ReflectionUtils";

    /**
     * 调用一个对象的隐藏方法。
     *
     * @param obj        调用方法的对象.
     * @param methodName 方法名。
     * @param types      方法的参数类型。
     * @param args       方法的参数。
     *
     * @return 如果调用成功，则返回true。
     */
    public static boolean invokeHideMethod(Object obj, String methodName, Class<?>[] types,
                                           Object[] args) {
        boolean hasInvoked = false;
        try {
            Class<?> cls;
            if (obj instanceof Class<?>) { // 静态方法
                cls = (Class<?>) obj;
            } else { // 非静态方法
                cls = obj.getClass();
            }
            Method method = cls.getMethod(methodName, types);
            method.invoke(obj, args);
            hasInvoked = true;
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked success!");
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked failed: " + e.getMessage());
            }
        }
        return hasInvoked;
    }

    /**
     * 调用一个对象的隐藏方法。
     *
     * @param obj        调用方法的对象.
     * @param methodName 方法名。
     * @param types      方法的参数类型。
     * @param args       方法的参数。
     *
     * @return 隐藏方法调用的返回值。
     */
    public static Object invokeHideMethodForObject(Object obj, String methodName, Class<?>[] types,
                                                   Object[] args) {
        Object o = null;
        try {
            Class<?> cls;
            if (obj instanceof Class<?>) { // 静态方法
                cls = (Class<?>) obj;
            } else { // 非静态方法
                cls = obj.getClass();
            }
            Method method = cls.getMethod(methodName, types);
            o = method.invoke(obj, args);
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked success!");
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked failed: " + e.getMessage());
            }
        }
        return o;
    }

    /**
     * 调用一个对象的私有方法。
     *
     * @param obj        调用方法的对象.
     * @param methodName 方法名。
     * @param types      方法的参数类型。
     * @param args       方法的参数。
     *
     * @return 如果调用成功，则返回true。
     */
    public static boolean invokeDeclaredMethod(Object obj, String methodName, Class<?>[] types,
                                               Object[] args) {
        boolean hasInvoked = false;
        try {
            Class<?> cls;
            if (obj instanceof Class<?>) { // 静态方法
                cls = (Class<?>) obj;
            } else { // 非静态方法
                cls = obj.getClass();
            }
            Method method = cls.getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            method.invoke(obj, args);
            hasInvoked = true;
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked success!");
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked failed: " + e.getMessage());
            }
        }
        return hasInvoked;
    }

    /**
     * 调用一个对象的私有方法。
     *
     * @param obj        调用方法的对象.
     * @param methodName 方法名。
     * @param types      方法的参数类型。
     * @param args       方法的参数。
     *
     * @return 私有方法调用的返回值。
     */
    public static Object invokeDeclaredMethodForObject(Object obj, String methodName,
                                                       Class<?>[] types, Object[] args) {
        Object o = null;
        try {
            Class<?> cls;
            if (obj instanceof Class<?>) { // 静态方法
                cls = (Class<?>) obj;
            } else { // 非静态方法
                cls = obj.getClass();
            }
            Method method = cls.getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            o = method.invoke(obj, args);
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked success!");
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Method \"" + methodName + "\" invoked failed: " + e.getMessage());
            }
        }
        return o;
    }
}
