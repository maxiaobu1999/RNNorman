package com.norman.util;

import java.text.DecimalFormat;

public class FloatUtil {
    /**
     * 保留小数点后2位
     * @param num 目标数字
     * @return 格式化后字符串
     */
    public static String decimalFormat(float num ) {
        return decimalFormat(num,"0.00");
    }

    /**
     * 保留小数点后n位
     * @param num 目标数字
     * @param format 格式，eg："0.00"
     * @return 格式化后字符串
     */
    public static String decimalFormat(float num, String format) {
        DecimalFormat df = new DecimalFormat(format);

        return df.format(num);

    }
}
