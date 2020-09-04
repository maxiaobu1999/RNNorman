package com.norman.util;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 公共类：获取厂商rom 版本
 * @since 2017/9/14.
 */
public class RomUtils {
    private static final String TAG = "Rom";
    public static final String PROP_RO_BUILD_FINGERPRINT = "ro.build.fingerprint";
    public static final String PROP_RO_BUILD_DISPLAY_ID = "ro.build.display.id";
    public static final String PROP_RO_BUILD_VERSION_INCREMENTAL = "ro.build.version.incremental";

    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_VIVO = "VIVO";
    public static final String ROM_QIKU = "QIKU";
    public static final String ROM_NUBIA = "NUBIA";
    public static final String ROM_UNKNOWN = "ROM_UNKNOWN";
    public static final String UNKNOWN = "UNKNOWN";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";
    private static final String KEY_VERSION_NUBIA = "ro.build.rom.id";

    // 金立手机rom version
    private static final String KEY_VERSION_GIONEE = "ro.gn.sv.version";

    /** 厂商名 */
    private static String sRomName;
    /** 厂商系统版本 */
    private static String sRomVersion;

    public static boolean isEmui() {
        return check(ROM_EMUI);
    }

    public static boolean isMiui() {
        return check(ROM_MIUI);
    }

    public static boolean isVivo() {
        return check(ROM_VIVO);
    }

    public static boolean isOppo() {
        return check(ROM_OPPO);
    }

    public static boolean isFlyme() {
        return check(ROM_FLYME);
    }

    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

    public static boolean isSmartisan() {
        return check(ROM_SMARTISAN);
    }

    public static boolean isNubia() {
        return check(ROM_NUBIA);
    }

    public static String getName() {
        if (sRomName == null) {
            check("");
        }
        return sRomName;
    }

    public static String getVersion() {
        if (sRomVersion == null) {
            check("");
        }
        return sRomVersion;
    }

    public static String getIncrementalVersion() {
        return getProp(PROP_RO_BUILD_VERSION_INCREMENTAL);
    }

    public static boolean check(String rom) {
        if (sRomName != null) {
            return sRomName.equals(rom);
        }

        if (!TextUtils.isEmpty(sRomVersion = getProp(KEY_VERSION_MIUI))) {
            sRomName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sRomVersion = getProp(KEY_VERSION_EMUI))) {
            sRomName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sRomVersion = getProp(KEY_VERSION_OPPO))) {
            sRomName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sRomVersion = getProp(KEY_VERSION_VIVO))) {
            sRomName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sRomVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sRomName = ROM_SMARTISAN;
        }  else if (!TextUtils.isEmpty(sRomVersion = getProp(KEY_VERSION_GIONEE))) {
            sRomName = ROM_SMARTISAN;
        } else if (!TextUtils.isEmpty(sRomVersion = getProp(KEY_VERSION_NUBIA))) {
            sRomName = ROM_NUBIA;
        } else {
            sRomVersion = Build.DISPLAY;
            if (sRomVersion.toUpperCase().contains(ROM_FLYME)) {
                sRomName = ROM_FLYME;
            } else {
                sRomVersion = Build.UNKNOWN;
                sRomName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sRomName.equals(rom);
    }

    public static String getProp(String name) {
        String line = UNKNOWN;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read prop " + name, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    /**
     * 是否为魅族 flyme系统
     * @return 魅族 flyme系统
     */
    public static boolean isFlymeQuickly() {
        String buildDisplay =  Build.DISPLAY;
        if (!TextUtils.isEmpty(buildDisplay) && buildDisplay.toUpperCase().contains(ROM_FLYME)) {
            return true;
        }
        return false;
    }
}
