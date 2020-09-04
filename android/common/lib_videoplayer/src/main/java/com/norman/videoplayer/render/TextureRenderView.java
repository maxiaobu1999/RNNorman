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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.Nullable;

import com.norman.videoplayer.Constants;
import com.norman.videoplayer.player.AbstractPlayer;
import com.norman.videoplayer.utils.MeasureHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 姓名：马庆龙 on 2019/3/21 11:06 AM
 * 功能：渲染view
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureRenderView extends TextureView implements IRenderView {
    private static final String TAG = "TextureRenderView+++";
    private Map<IRenderCallback, Object> mRenderCallbackMap = new ConcurrentHashMap<IRenderCallback, Object>();
    private MeasureHelper mMeasureHelper;
    @Nullable
    private AbstractPlayer mMediaPlayer;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    public TextureRenderView(Context context) {
        super(context);
        initView();
    }

    public TextureRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {
        /** SurfaceTexture可用时调用 */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (Constants.DEBUG)
            Log.d(TAG, "onSurfaceTextureAvailable:surfaceTexture=" + surfaceTexture);
//            if (mSurfaceTexture != null) {
//                try {
//                    setSurfaceTexture(mSurfaceTexture);
//                } catch (IllegalArgumentException e) {
//                    // mSurfaceTexture 已经release()了 版本兼容
//                }
//            } else {
                mSurfaceTexture = surfaceTexture;
                mSurface = new Surface(surfaceTexture);
                if (mMediaPlayer != null) {
                    mMediaPlayer.setSurface(mSurface);
                }
//            }
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceCreated(TextureRenderView.this, width, height);
            }
        }

        /** 当SurfaceTexture缓冲区大小更改时调用 */
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            if (Constants.DEBUG)
                Log.d(TAG, "onSurfaceTextureSizeChanged:surfaceTexture=" + surfaceTexture);
//            mSurfaceTexture = surfaceTexture;
//            mSurface = new Surface(surfaceTexture);
//            if (mMediaPlayer != null) {
//                mMediaPlayer.setSurface(mSurface);
//            }
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceChanged(TextureRenderView.this, 0, width, height);
            }
        }

        /** 当指定SurfaceTexture即将被销毁时调用。如果返回true，则调用此方法后，表面纹理中不会发生渲染。
         * 如果返回false，则客户端需要调用release()。大多数应用程序应该返回 true。 */
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (Constants.DEBUG)
                Log.d(TAG, "onSurfaceTextureDestroyed: destroy: " + surface);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceDestroyed(TextureRenderView.this);
            }
            return true;
        }

        /** 当指定SurfaceTexture的更新时调用updateTexImage()。 */
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            Log.d(TAG, "onSurfaceTextureUpdated");

        }
    };

    private void initView() {
        mMeasureHelper = new MeasureHelper();
        setSurfaceTextureListener(surfaceTextureListener);
        SurfaceTexture surfaceTexture = getSurfaceTexture();
        if (Constants.DEBUG)
            Log.d(TAG, "surfaceTexture+++:" + surfaceTexture);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void attachToPlayer(AbstractPlayer mp) {
        this.mMediaPlayer = mp;
        if (mSurface != null) {
            mMediaPlayer.setSurface(mSurface);
        }
    }

    @Override
    public boolean shouldWaitForResize() {
        return false;
    }

    @Override
    public void release() {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }

        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }


    @Override
    public void addRenderCallback(IRenderCallback callback) {
        mRenderCallbackMap.put(callback, callback);
    }

    @Override
    public void removeRenderCallback(IRenderCallback callback) {
        mRenderCallbackMap.remove(callback);
    }


}
