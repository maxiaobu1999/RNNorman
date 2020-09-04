package com.norman.videoplayer.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.videoplayer.R;
import com.norman.videoplayer.Constants;
import com.norman.videoplayer.controller.base.IControlComponent;
import com.norman.videoplayer.utils.PlayerUtils;
import com.norman.videoplayer.ui.ControlVideoView;


/**
 * 点播底部控制栏
 */
public class VodControlView extends FrameLayout implements IControlComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected ControlVideoView mVideoView;

    private TextView mTotalTime, mCurrTime;
    private ImageView mFullScreen;
    private LinearLayout mBottomContainer;
    private SeekBar mVideoProgress;
    private ProgressBar mBottomProgress;
    private ImageView mPlayButton;

    private boolean mIsDragging;

    private boolean mIsShowBottomProgress = true;

    public VodControlView(@NonNull Context context) {
        super(context);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        mFullScreen = findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        mBottomContainer = findViewById(R.id.bottom_container);
        mVideoProgress = findViewById(R.id.seekBar);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mTotalTime = findViewById(R.id.total_time);
        mCurrTime = findViewById(R.id.curr_time);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mBottomProgress = findViewById(R.id.bottom_progress);
    }

    protected int getLayoutId() {
        return R.layout.video_player_layout_vod_control_view;
    }

    /**
     * 是否显示底部进度条，默认显示
     */
    public void showBottomProgress(boolean isShow) {
        mIsShowBottomProgress = isShow;
    }

    @Override
    public void attach(@NonNull ControlVideoView videoView) {
        mVideoView = videoView;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void show(Animation showAnim) {
        setVisibility(VISIBLE);
        mBottomContainer.setVisibility(VISIBLE);
        if (showAnim != null) {
            mBottomContainer.startAnimation(showAnim);
        }
        if (mIsShowBottomProgress) {
            mBottomProgress.setVisibility(GONE);
        }
    }

    @Override
    public void hide(Animation hideAnim) {
        mBottomContainer.setVisibility(GONE);
        if (hideAnim != null) {
            mBottomContainer.startAnimation(hideAnim);
        }
        if (mIsShowBottomProgress) {
            mBottomProgress.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case Constants.STATE_IDLE:
            case Constants.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                break;
            case Constants.STATE_START_ABORT:
            case Constants.STATE_PREPARING:
            case Constants.STATE_PREPARED:
            case Constants.STATE_ERROR:
                setVisibility(GONE);
                break;
            case Constants.STATE_PLAYING:
                mPlayButton.setSelected(mVideoView.isPlaying());
                if (mIsShowBottomProgress) {
                    if (mVideoView.isShowing()) {
                        mBottomProgress.setVisibility(GONE);
                        mBottomContainer.setVisibility(VISIBLE);
                    } else {
                        mBottomContainer.setVisibility(GONE);
                        mBottomProgress.setVisibility(VISIBLE);
                    }
                } else {
                    mBottomContainer.setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mVideoView.startProgress();
                break;
            case Constants.STATE_PAUSED:
            case Constants.STATE_BUFFERING:
            case Constants.STATE_BUFFERED:
                mPlayButton.setSelected(mVideoView.isPlaying());
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case Constants.PLAYER_NORMAL:
                mFullScreen.setSelected(false);
                break;
            case Constants.PLAYER_FULL_SCREEN:
                mFullScreen.setSelected(true);
                break;
        }
    }

    @Override
    public void adjustView(int orientation, int space) {
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mBottomContainer.setPadding(0, 0, 0, 0);
            mBottomProgress.setPadding(0, 0, 0, 0);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mBottomContainer.setPadding(space, 0, 0, 0);
            mBottomProgress.setPadding(space, 0, 0, 0);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            mBottomContainer.setPadding(0, 0, space, 0);
            mBottomProgress.setPadding(0, 0, space, 0);
        }
    }

    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }

        if (mVideoProgress != null) {
            if (duration > 0) {
                mVideoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
                mVideoProgress.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoProgress.setEnabled(false);
            }
            int percent = mVideoView.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoProgress.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTotalTime != null)
            mTotalTime.setText(PlayerUtils.stringForTime(duration));
        if (mCurrTime != null)
            mCurrTime.setText(PlayerUtils.stringForTime(position));
    }

    @Override
    public void onLock() {
        hide(null);
    }

    @Override
    public void onUnlock() {
        show(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mVideoView.togglePlay();
        }
    }

    /** 横竖屏切换 */
    private void toggleFullScreen() {
        mVideoView.toggleFullScreen();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mVideoView.stopProgress();
        mVideoView.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mVideoView.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
        mVideoView.seekTo((int) newPosition);
        mIsDragging = false;
        mVideoView.startProgress();
        mVideoView.startFadeOut();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mVideoView.getDuration();
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        if (mCurrTime != null)
            mCurrTime.setText(PlayerUtils.stringForTime((int) newPosition));
    }
}
