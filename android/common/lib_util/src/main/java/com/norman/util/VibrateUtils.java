package com.norman.util;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

/**
 * 振动器实例支持波形震动和单震动由timings控制，必须传；振幅amplitudes由于系统API版本可选
 *
 * @since 2018/6/6
 */
public class VibrateUtils {
    /** 振动器对象 Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); */
    private Vibrator mVibrator;
    /** 振幅值中的定时值。定时值为0振幅可忽视的 */
    private long[] timings;
    /** 振幅值中的振幅值。振幅值必须为0和255之间，或为VibrationEffect.DEFAULT_AMPLITUDE。振幅值为0意味着断开 ～～～可传可不传 */
    private int[] amplitudes;
    /** 上下文对象 */
    private Context mContext;

    private VibrateUtils(Builder builder) {
        mVibrator = builder.mVibrator;
        timings = builder.timings;
        amplitudes = builder.amplitudes;
        mContext = builder.mContext;
    }

    /**
     * 创建振动
     */
    @RequiresPermission(Manifest.permission.VIBRATE)
    public void vibrateStart() {
        if (!checkVibratePermission() || mVibrator == null || timings == null) {
            return;
        }
        if (timings.length <= 0) {
            return;
        }
        if (mVibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (amplitudes != null && amplitudes.length > 0) {
                    if (timings.length == amplitudes.length) {
                        if (timings.length == 1) {
                            mVibrator.vibrate(VibrationEffect.createOneShot(timings[0], amplitudes[0]));
                        } else {
                            mVibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1));
                        }
                    } else {
                        if (timings.length == 1) {
                            mVibrator.vibrate(VibrationEffect.createOneShot(timings[0], amplitudes[0]));
                        } else {
                            mVibrator.vibrate(VibrationEffect.createWaveform(timings, -1));
                        }
                    }
                } else {
                    if (timings.length == 1) {
                        mVibrator.vibrate(VibrationEffect.createOneShot(timings[0], DEFAULT_AMPLITUDE));
                    } else {
                        mVibrator.vibrate(VibrationEffect.createWaveform(timings, -1));
                    }
                }
            } else {
                if (timings.length == 1) {
                    mVibrator.vibrate(timings[0]);
                } else {
                    mVibrator.vibrate(timings, -1);
                }
            }
        }
    }

    /**
     * 取消震动
     */
    @RequiresPermission(Manifest.permission.VIBRATE)
    public void cancelVibrator() {
        if (mVibrator != null && mVibrator.hasVibrator() && checkVibratePermission()) {
            mVibrator.cancel();
        }
    }

    /**
     * 检查是否有振动权限
     *
     * @return true，有振动权限；false，无振动权限
     */
    private boolean checkVibratePermission() {
        if (mContext == null) {
            return false;
        }
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.VIBRATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static final class Builder {
        private Vibrator mVibrator;
        private long[] timings;
        private int[] amplitudes;
        private Context mContext;

        /**
         * Builder 构造函数
         *
         * @param mVibrator 震动器句柄
         * @param timings   振幅值中的定时值。定时值为0振幅可忽视的
         * @param mContext  上下文
         */
        public Builder(Vibrator mVibrator, long[] timings, Context mContext) {
            this.mVibrator = mVibrator;
            this.mContext = mContext;
            this.timings = timings;
        }

        /**
         * 设置振幅值
         *
         * @param amplitudes 振幅值中的振幅值。振幅值必须为0和255之间，或为DEFAULT_AMPLITUDE。振幅值为0意味着断开 ～～～可传可不传
         * @return Builder
         */
        public Builder amplitudes(int[] amplitudes) {
            this.amplitudes = amplitudes;
            return this;
        }

        public VibrateUtils build() {
            return new VibrateUtils(this);
        }
    }
}