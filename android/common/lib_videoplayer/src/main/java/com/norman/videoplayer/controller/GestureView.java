package com.norman.videoplayer.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.videoplayer.R;
import com.norman.videoplayer.Constants;
import com.norman.videoplayer.controller.base.IGestureComponent;
import com.norman.videoplayer.utils.PlayerUtils;
import com.norman.videoplayer.ui.ControlVideoView;

/**
 * 手势控制
 */
public class GestureView extends FrameLayout implements IGestureComponent {

    public GestureView(@NonNull Context context) {
        super(context);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ControlVideoView mMediaPlayer;

    private ImageView mIcon;
    private ProgressBar mProgressPercent;
    private TextView mTextPercent;

    private LinearLayout mCenterContainer;


    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.video_player_layout_gesture_control_view, this, true);
        mIcon = findViewById(R.id.iv_icon);
        mProgressPercent = findViewById(R.id.pro_percent);
        mTextPercent = findViewById(R.id.tv_percent);
        mCenterContainer = findViewById(R.id.center_container);
    }

    @Override
    public void attach(@NonNull ControlVideoView videoView) {
        mMediaPlayer = videoView;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void onStartSlide() {
        mMediaPlayer.hideInner();
        mCenterContainer.setVisibility(VISIBLE);
        mCenterContainer.setAlpha(1f);
    }

    @Override
    public void onStopSlide() {
        mCenterContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mCenterContainer.setVisibility(GONE);
                    }
                })
                .start();
    }

    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        mProgressPercent.setVisibility(GONE);
        if (slidePosition > currentPosition) {
            mIcon.setImageResource(R.drawable.video_player_ic_action_fast_forward);
        } else {
            mIcon.setImageResource(R.drawable.video_player_ic_action_fast_rewind);
        }
        mTextPercent.setText(String.format("%s/%s", PlayerUtils.stringForTime(slidePosition), PlayerUtils.stringForTime(duration)));
    }

    @Override
    public void onBrightnessChange(int percent) {
        mProgressPercent.setVisibility(VISIBLE);
        mIcon.setImageResource(R.drawable.video_player_ic_action_brightness);
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void onVolumeChange(int percent) {

        mProgressPercent.setVisibility(VISIBLE);
        if (percent <= 0) {
            mIcon.setImageResource(R.drawable.video_player_ic_action_volume_off);
        } else {
            mIcon.setImageResource(R.drawable.video_player_ic_action_volume_up);
        }
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void show(Animation showAnim) {

    }

    @Override
    public void hide(Animation hideAnim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == Constants.STATE_IDLE
                || playState == Constants.STATE_START_ABORT
                || playState == Constants.STATE_PREPARING
                || playState == Constants.STATE_PREPARED
                || playState == Constants.STATE_ERROR
                || playState == Constants.STATE_PLAYBACK_COMPLETED) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void adjustView(int orientation, int space) {

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
