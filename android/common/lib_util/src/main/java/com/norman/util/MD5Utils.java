

package com.norman.util;

import com.norman.runtime.AppRuntime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 公共类：MD5 相关操作。
 *
 * @since 16/8/18
 */
public class MD5Utils {
    /** 全局debug开关 */
    private static final boolean DEBUG = AppRuntime.isDebug();
    /** File buffer stream size */
    public static final int FILE_STREAM_BUFFER_SIZE = 8192;

    /**
     * 把二进制byte数组生成 md5 32位 十六进制字符串，单个字节小于0xf，高位补0。
     *
     * @param bytes 输入
     * @param upperCase true：大写， false 小写字符串
     * @return 把二进制byte数组生成 md5 32位 十六进制字符串，单个字节小于0xf，高位补0。
     */
    public static String toMd5(byte[] bytes, boolean upperCase) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            return toHexString(algorithm.digest(), "", upperCase);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算文件Md5 32位 十六进制字符串，单个字节小于0xf，高位补0
     *
     * @param file 文件
     * @param upperCase true：大写， false 小写字符串
     * @return Md5 32位 十六进制字符串，单个字节小于0xf，高位补0
     */
    public static String toMd5(File file, boolean upperCase) {
        InputStream is = null;
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            is = new FileInputStream(file);
            byte[] buffer = new byte[FILE_STREAM_BUFFER_SIZE];
            int read = 0;
            while ((read = is.read(buffer)) > 0) {
                algorithm.update(buffer, 0, read);
            }
            return toHexString(algorithm.digest(), "", upperCase);
        } catch (NoSuchAlgorithmException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
     *
     * @param bytes 输入
     * @param separator 分割线
     * @param upperCase true：大写， false 小写字符串
     * @return 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
     */
    private static String toHexString(byte[] bytes, String separator, boolean upperCase) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xFF & b); // SUPPRESS CHECKSTYLE
            if (upperCase) {
                str = str.toUpperCase();
            }
            if (str.length() == 1) {
                hexString.append("0");
            }
            hexString.append(str).append(separator);
        }
        return hexString.toString();
    }
}
