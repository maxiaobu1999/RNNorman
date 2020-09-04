

package com.norman.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.norman.runtime.AppRuntime;


/**
 * 公共类：网络相关工具方法
 */


public class NetWorkUtils {
    private static final boolean DEBUG = AppRuntime.isDebug();
//    private static final boolean DEBUG = LibUtilConfig.GLOBAL_DEBUG;
    private static final String TAG = "NetWorkUtils";
    /** 网络类型:WIFI */
    public static final String NETWORK_TYPE_WIFI = "wifi";
    /** 网络类型:2G */
    public static final String NETWORK_TYPE_CELL_2G = "2g";
    /** 网络类型:3G */
    public static final String NETWORK_TYPE_CELL_3G = "3g";
    /** 网络类型:4G */
    public static final String NETWORK_TYPE_CELL_4G = "4g";
    /** 网络类型:未知 */
    public static final String NETWORK_TYPE_CELL_UNKNOWN = "unknown";
    /** 网络类型:未连接 */
    public static final String NETWORK_TYPE_CELL_UN_CONNECTED = "no";

    /** LTE网络类型：LTE_CA，属于4G范畴 */
    public static final int NETWORK_TYPE_LTE_CA = 19;

    /**
     * 网络类型。
     */
    public enum NetType {
        NONE("no"), WIFI("wifi"), _2G("2g"), _3G("3g"), _4G("4g"), UNKOWN("unknow");

        /** 类型 */
        public final String type;

        NetType(String type) {
            this.type = type;
        }
    }

    /**
     * 获取活动的连接。
     *
     * @param context context
     * @return 当前连接。ContextUtils初始化失败时可能会返回Null
     */
    public static NetworkInfo getActiveNetworkInfo(Context context) {
        Context appContext = AppRuntime.getAppContext();
        if (null == appContext) {
            return null;
        }
        ConnectivityManager connectivity =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }
        return connectivity.getActiveNetworkInfo();
    }

    /**
     * wifi网络是否可用
     *
     * @param context context
     * @return wifi连接并可用返回 true
     */
    public static boolean isWifiNetworkConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(AppRuntime.getAppContext());
        // return networkInfo != null && networkInfo.isConnected();
        boolean flag = networkInfo != null && networkInfo.isAvailable()
                && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
        if (DEBUG) {
            Log.d(TAG, "isWifiNetworkConnected, rtn: " + flag);
        }
        return flag;
    }

    /**
     * 数据网络是否可用
     *
     * @param context context
     * @return 数据网络连接并可用返回 true
     */
    public static boolean isMobileNetworkConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(AppRuntime.getAppContext());
        boolean flag = networkInfo != null && networkInfo.isAvailable()
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        if (DEBUG) {
            Log.d(TAG, "isMobileNetworkConnected, rtn: " + flag);
        }
        return flag;
    }

    /**
     * 网络是否可用。(
     *
     * @param context context
     * @return 连接并可用返回 true
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(AppRuntime.getAppContext());
        // return networkInfo != null && networkInfo.isConnected();
        boolean flag = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (DEBUG) {
            Log.d(TAG, "isNetworkConnected, rtn: " + flag);
        }
        return flag;
    }

    /**
     * 获取当前网络类型
     * <br/>
     * no: not connected
     * wifi/2g/3g/4g: net type
     * unknown: unknown net type
     * <br/>
     * @return wifi/2g/3g/4g/unknown
     */
    public static String getNetworkClass() {
        NetworkInfo info = getActiveNetworkInfo(AppRuntime.getAppContext());
        if (info == null || !info.isConnected()) {
            return NETWORK_TYPE_CELL_UN_CONNECTED; //not connected
        }
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_TYPE_WIFI;
        }
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            String subTypeName = info.getSubtypeName();
            return getMobileNetworkType(networkType, subTypeName);
        }
        return NETWORK_TYPE_CELL_UNKNOWN;
    }

    /**
     * 获取简化的移动网络类型。
     * @param netType, TelephonyManager中定义的移动网络类型
     * @param subTypeName 网络类型的文字描述
     * @see NetworkInfo
     * @return 返回网络类型。
     */
    public static String getMobileNetworkType(int netType, String subTypeName) {
        if (DEBUG) {
            Log.d("NetWorkUtils", "——> getNetworkType: netType " + netType + " subTypeName " + subTypeName);
        }
        switch (netType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_TYPE_CELL_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_TYPE_CELL_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
            case NETWORK_TYPE_LTE_CA:
                return NETWORK_TYPE_CELL_4G;
            default:
                // 移动4G网络下,subTypeName:LTE_CA,subType=139
                if (!TextUtils.isEmpty(subTypeName) && subTypeName.equalsIgnoreCase("LTE_CA")) {
                    return NETWORK_TYPE_CELL_4G;
                } else {
                    return NETWORK_TYPE_CELL_UNKNOWN;
                }
        }
    }

    /**
     * 获取可枚举的网络类型。
     * @see NetType
     *
     * @return 返回可枚举的网络类型。
     */
    public static NetType getNetworkType() {
        String netType = NetWorkUtils.getNetworkClass();
        switch (netType) {
            case "2g":
                return NetType._2G;
            case "3g":
                return NetType._3G;
            case "4g":
                return NetType._4G;
            case "wifi":
                return NetType.WIFI;
            case "no":
                return NetType.NONE;
            case "unknow":
            default:
                return NetType.UNKOWN;
        }
    }

    /**
     * 获取是否是高速网络(3G,4G,WIFI)
     *
     * @return 高速网络是否可用
     */
    public static boolean isHighNetworkConnected() {
        String netType = getNetworkClass();
        return NETWORK_TYPE_WIFI.equals(netType) || NETWORK_TYPE_CELL_4G.equals(netType)
                || NETWORK_TYPE_CELL_3G.equals(netType);
    }
}
