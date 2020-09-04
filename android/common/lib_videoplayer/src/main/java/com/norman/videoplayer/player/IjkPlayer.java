package com.norman.videoplayer.player;

import android.app.Application;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.norman.runtime.AppRuntime;
import com.norman.videoplayer.VideoViewManager;
import com.norman.videoplayer.utils.RawDataSourceProvider;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 解码器实现 基于ijkPlayer
 */
public class IjkPlayer extends AbstractPlayer {
    private IjkMediaPlayer mMediaPlayer;
    /** 缓冲百分比 */
    private int mBufferedPercent;
    /** 同步准备锁 */
    private ReentrantLock mPrepareLock= new ReentrantLock();

    @Override
    public void initPlayer() {
        mMediaPlayer = new IjkMediaPlayer();
        //native日志
        IjkMediaPlayer.native_setLogLevel(VideoViewManager.getConfig().mIsEnableLog ? IjkMediaPlayer.IJK_LOG_INFO : IjkMediaPlayer.IJK_LOG_SILENT);

        // prepared后自动start false
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
//        ////硬解码：1、打开，0、关闭
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        setOptions();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
//        mMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
//            @Override
//            public boolean onNativeInvoke(int i, Bundle bundle) {
//                return true;
//            }
//        });
    }

    @Override
    public void setOptions() {
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        try {
            Application application = AppRuntime.getApplication();
            if (application != null) {
                Uri uri = Uri.parse(path);
                if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())) {
                    // 处理播放raw中的文件
                    RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(application, uri);
                    mMediaPlayer.setDataSource(rawDataSourceProvider);
                } else {
                    // 处理UA问题
                    if (headers != null) {
                        // User-Agent会告诉网站服务器，访问者是通过什么工具来请求的
                        String userAgent = headers.get("User-Agent");
                        if (!TextUtils.isEmpty(userAgent)) {
                            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent);
                        }
                    }
                    mMediaPlayer.setDataSource(application, uri, headers);
                }
            }
        } catch (Exception e) {
            mPlayerEventListener.onError(0, "setDataSource():" + e.getMessage());
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mMediaPlayer.setDataSource(new RawDataSourceProvider(fd));
        } catch (Exception e) {
            mPlayerEventListener.onError(0, "setDataSource(AssetFileDescriptor fd):" + e.getMessage());
        }
    }

    @Override
    public void start() {
        try {
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(0, "start():" + e.getMessage());
        }
    }

    @Override
    public void pause() {
        try {
            mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(0, "pause():" + e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            mMediaPlayer.stop();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(0, "stop():" + e.getMessage());
        }
    }

    @Deprecated
    @Override
    public void prepare() {
        try {
            mMediaPlayer.prepareAsync();
            mPrepareLock.tryLock();
        } catch (Exception e) {
            mPlayerEventListener.onError(0, "prepare():" + e.getMessage());
        }
    }

    @Override
    public void prepareAsync() {
        try {
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(0, "prepareAsync():" + e.getMessage());
        }
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        setOptions();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        try {
            mMediaPlayer.seekTo((int) time);
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(0, "seekTo():" + e.getMessage());
        }
    }

    @Override
    public void release() {
        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnInfoListener(null);
        mMediaPlayer.setOnBufferingUpdateListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnVideoSizeChangedListener(null);
        new Thread() {
            @Override
            public void run() {
                try {
                    mMediaPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return mBufferedPercent;
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void setVolume(float v1, float v2) {
        mMediaPlayer.setVolume(v1, v2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        mMediaPlayer.setLooping(isLooping);
    }

    @Override
    public void setSpeed(float speed) {
        mMediaPlayer.setSpeed(speed);
    }

    @Override
    public long getTcpSpeed() {
        return mMediaPlayer.getTcpSpeed();
    }


    /*===============回调BEGIN=================*/
    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
            mPlayerEventListener.onError(-1, "IMediaPlayer.OnErrorListener");
            return true;
        }
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            mPlayerEventListener.onCompletion();
        }
    };

    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            mPlayerEventListener.onInfo(what, extra);
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            mBufferedPercent = percent;
        }
    };


    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if (mPrepareLock.isLocked()) {
                mPrepareLock.unlock();
            }
            mPlayerEventListener.onPrepared();
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
            }
        }
    };
}
