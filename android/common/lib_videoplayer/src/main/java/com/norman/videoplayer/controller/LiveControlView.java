package com.norman.videoplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.videoplayer.R;
import com.norman.videoplayer.Constants;
import com.norman.videoplayer.controller.base.IControlComponent;
import com.norman.videoplayer.utils.PlayerUtils;
import com.norman.videoplayer.ui.ControlVideoView;

/**
 * 直播底部控制栏
 */
public class LiveControlView extends FrameLayout implements IControlComponent, View.OnClickListener {

    private ControlVideoView mMediaPlayerWrapper;

    private ImageView mFullScreen;
    private LinearLayout mBottomContainer;
    private ImageView mPlayButton;

    public LiveControlView(@NonNull Context context) {
        super(context);
    }

    public LiveControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    
    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.video_player_layout_live_control_view, this, true);
        mFullScreen = findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        mBottomContainer = findViewById(R.id.bottom_container);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        ImageView refresh = findViewById(R.id.iv_refresh);
        refresh.setOnClickListener(this);
    }

    @Override
    public void attach(@NonNull ControlVideoView videoView) {
        mMediaPlayerWrapper = videoView;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void show(Animation showAnim) {
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
            if (showAnim != null) {
                startAnimation(showAnim);
            }
        }
    }

    @Override
    public void hide(Animation hideAnim) {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
            if (hideAnim != null) {
                startAnimation(hideAnim);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case Constants.STATE_IDLE:
            case Constants.STATE_START_ABORT:
            case Constants.STATE_PREPARING:
            case Constants.STATE_PREPARED:
            case Constants.STATE_ERROR:
            case Constants.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                break;
            case Constants.STATE_PLAYING:
            case Constants.STATE_PAUSED:
            case Constants.STATE_BUFFERING:
            case Constants.STATE_BUFFERED:
                mPlayButton.setSelected(mMediaPlayerWrapper.isPlaying());
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
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mBottomContainer.setPadding(space, 0, 0, 0);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            mBottomContainer.setPadding(0, 0, space, 0);
        }
    }

    @Override
    public void setProgress(int duration, int position) {

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
            mMediaPlayerWrapper.togglePlay();
        } else if (id == R.id.iv_refresh) {
            mMediaPlayerWrapper.replay(true);
        }
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mMediaPlayerWrapper.toggleFullScreen();
    }
}
