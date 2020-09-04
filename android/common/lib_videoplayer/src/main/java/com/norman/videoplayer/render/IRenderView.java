/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.norman.videoplayer.render;

import android.view.View;

import androidx.annotation.NonNull;

import com.norman.videoplayer.player.AbstractPlayer;

/**
 * 姓名：马庆龙 on 2019/3/21 11:07 AM
 * 功能：渲染view
 * 实现类：SurfaceRenderView 、 TextureRenderView
 */
public interface IRenderView {
    /** 返回this */
    View getView();

    /** 关联MediaPlayer */
    void attachToPlayer(AbstractPlayer mp);

    boolean shouldWaitForResize();

    /**
     * 设置视频宽高
     *
     * @param videoWidth  宽
     * @param videoHeight 高
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 设置视频旋转角度
     *
     * @param degree 角度值
     */
    void setVideoRotation(int degree);

    /** 填从方式 */
    void setAspectRatio(int aspectRatio);

    void addRenderCallback(@NonNull IRenderCallback callback);

    void removeRenderCallback(@NonNull IRenderCallback callback);

    /** 释放资源 */
    void release();

//    /** 对Surface&Texture的封装 */
//    interface ISurfaceHolder {
//
//        @NonNull
//        IRenderView getRenderView();
//
//        @Nullable
//        SurfaceHolder getSurfaceHolder();
//
//        @Nullable
//        Surface openSurface();
//
//        @Nullable
//        SurfaceTexture getSurfaceTexture();
//    }

    interface IRenderCallback {
        /**
         * @param holder
         * @param width  could be 0
         * @param height could be 0
         */
        void onSurfaceCreated(@NonNull IRenderView holder, int width, int height);

        /**
         * @param holder
         * @param format could be 0
         * @param width
         * @param height
         */
        void onSurfaceChanged(@NonNull IRenderView holder, int format, int width, int height);

        void onSurfaceDestroyed(@NonNull IRenderView holder);
    }
}
