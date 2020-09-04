package com.norman.videoplayer.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.videoplayer.constant.enums.PlayState;
import com.norman.videoplayer.listener.OnPrepareListener;

/**
 * 实现非核心常用功能
 * 1、播放进度
 * 2、全屏
 * 3、播放暂停
 */
public class BaseVideoView extends MQLVideoView {
    /** 当前播放进度 毫秒 */
    protected long mCurrentPosition;


    public BaseVideoView(@NonNull Context context) {
        super(context);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void play(String url) {
        if (mMediaPlayer == null) {
            initMediaPlayer();
        } else {
            reset();
        }
        setDataSource(url);
        mCurrentPosition = 0;
        prepareAsync();
        setOnPrepareListener(new OnPrepareListener() {
            @Override
            public void onPrepared(MQLVideoView videoView) {
                start();
            }
        });
    }

    protected void startOnPrepared() {
        prepareAsync();
        setOnPrepareListener(new OnPrepareListener() {
            @Override
            public void onPrepared(MQLVideoView videoView) {
                start();
            }
        });
    }


    /**
     * 重播
     *
     * @param resetPosition 是否从头开始播放
     */
    public void replay(boolean resetPosition) {
        if (resetPosition) {
            mCurrentPosition = 0;
            seekTo(mCurrentPosition);
            start();
        } else {
            startOnPrepared();

        }
    }



    /** 获取当前缓冲百分比*/
    public int getBufferedPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercentage() : 0;
    }


    /** 获取播放进度百分比*/
    public int getCurrentPercent() {
        if (isInPlaybackState()) {
            return (int) (getCurrentPosition() / getDuration());
        }
        return 0;
    }

    /** 获取视频总时长*/
    public long getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /** 获取当前播放的位置*/
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            return mCurrentPosition;
        }
        return 0;
    }

    /** 当前正在播放 */
    public boolean isPlaying() {
        if (isInPlaybackState()) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /** 设置过视频地址 */
    public boolean hasDataSource() {
        return mUrl == null;
    }

    /** 获取过视频地址 */
    public String getDataSource() {
        return mUrl;
    }

//    /** 获取过视频地址 */
//    public void setDataSource(String url) {
//    }

    /** 是否处于可播放状态*/
    public boolean isInPlaybackState() {
        return mMediaPlayer != null
                && (mCurrentState == PlayState.STATE_STARTED
                || mCurrentState == PlayState.STATE_PAUSED
                || mCurrentState == PlayState.STATE_PLAYBACK_COMPLETED);
    }

    /** 播放和暂停*/
    public void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

}
