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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.videoplayer.R;
import com.norman.videoplayer.Constants;
import com.norman.videoplayer.controller.base.IControlComponent;
import com.norman.videoplayer.utils.PlayerUtils;
import com.norman.videoplayer.ui.ControlVideoView;

/**
 * 自动播放完成界面
 */
public class CompleteView extends FrameLayout implements IControlComponent {

    private ControlVideoView mMediaPlayerWrapper;

    private ImageView mStopFullscreen;

    public CompleteView(@NonNull Context context) {
        super(context);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.video_player_layout_complete_view, this, true);
        findViewById(R.id.iv_replay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayerWrapper.replay(true);
            }
        });
        mStopFullscreen = findViewById(R.id.stop_fullscreen);
        mStopFullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayerWrapper.isFullScreen()) {
                    Activity activity = PlayerUtils.scanForActivity(getContext());
                    if (activity != null && !activity.isFinishing()) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mMediaPlayerWrapper.stopFullScreen();
                    }
                }
            }
        });
        setClickable(true);
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

    }

    @Override
    public void hide(Animation hideAnim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState ==Constants. STATE_PLAYBACK_COMPLETED) {
            setVisibility(VISIBLE);
            mStopFullscreen.setVisibility(mMediaPlayerWrapper.isFullScreen() ? VISIBLE : GONE);
            bringToFront();
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == Constants.PLAYER_FULL_SCREEN) {
            mStopFullscreen.setVisibility(VISIBLE);
        } else if (playerState == Constants.PLAYER_NORMAL) {
            mStopFullscreen.setVisibility(GONE);
        }
    }

    @Override
    public void adjustView(int orientation, int space) {
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
            sflp.setMargins(0, 0, 0, 0);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
            sflp.setMargins(space, 0, 0, 0);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
            sflp.setMargins(0, 0, 0, 0);
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLock() {

    }

    @Override
    public void onUnlock() {

    }
}
