package com.norman.util;

import android.text.TextUtils;
import android.util.Log;

import com.norman.runtime.AppRuntime;

public class CommonUtils {
    private static final String TAG = "CommonUtils+++";
    public static int getMixLength(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        float length = 0f;
        char[] chars = str.toCharArray();
        for (char c : chars) {
            // 中文
            if ((c >= 0x0391 && c <= 0xFFE5)) {
                length += 1;
            } else {
                // 其他字符
                length += 0.5;
            }
        }
        return (int) (length + 0.5);
    }

    public static long convertStringToLongSafe(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0L;
        }
        long ret = 0L;
        try {
            ret = Long.parseLong(string);
        } catch (NumberFormatException e) {
            if (AppRuntime.isDebug()) {
                Log.d(TAG, "convert string to long error");
            }
        }
        return ret;
    }

}
