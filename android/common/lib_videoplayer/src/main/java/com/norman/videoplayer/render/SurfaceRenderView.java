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

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.norman.videoplayer.player.AbstractPlayer;
import com.norman.videoplayer.utils.MeasureHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 姓名：马庆龙 on 2019/3/21 11:06 AM
 * 功能：渲染view
 */
public class SurfaceRenderView extends SurfaceView implements IRenderView {
    private static final String TAG = "SurfaceRenderView+++";
    private Map<IRenderCallback, Object> mRenderCallbackMap = new ConcurrentHashMap<IRenderCallback, Object>();
    private MeasureHelper mMeasureHelper;

    private AbstractPlayer mMediaPlayer;

    public SurfaceRenderView(Context context) {
        super(context);
    }

    public SurfaceRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfaceRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mMeasureHelper = new MeasureHelper();
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "onSurfaceTextureAvailable:surfaceTexture="+holder);
                for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                    renderCallback.onSurfaceCreated(SurfaceRenderView.this, 0, 0);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(holder);
                }
                Log.d(TAG, "onSurfaceTextureSizeChanged:surfaceTexture="+holder);
                for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                    renderCallback.onSurfaceChanged(SurfaceRenderView.this, 0, width, height);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "onSurfaceTextureDestroyed: destroy: " + holder);
                for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                    renderCallback.onSurfaceDestroyed(SurfaceRenderView.this);
                }
            }
        });
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void attachToPlayer(@NonNull AbstractPlayer player) {
        this.mMediaPlayer = player;
    }

    @Override
    public boolean shouldWaitForResize() {
        return false;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotation(degree);
        setRotation(degree);
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }



    @Override
    public View getView() {
        return this;
    }


    @Override
    public void release() {

    }

    @Override
    public void addRenderCallback(@NonNull IRenderCallback callback) {
        mRenderCallbackMap.put(callback,callback);
    }

    @Override
    public void removeRenderCallback(@NonNull IRenderCallback callback) {
        mRenderCallbackMap.remove(callback);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }
}
