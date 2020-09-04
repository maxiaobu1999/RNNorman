package com.norman.videoplayer.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.util.Log;
import com.norman.videoplayer.R;
import com.norman.videoplayer.Constants;
import com.norman.videoplayer.controller.base.GestureVideoController;
import com.norman.videoplayer.utils.PlayerUtils;

/**
 * 直播/点播控制器
 */
public class StandardVideoController extends GestureVideoController implements View.OnClickListener {
    protected ImageView mLockButton;
    protected ProgressBar mLoadingProgress;

    public StandardVideoController(@NonNull Context context) {
        this(context, null);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_player_layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        mLockButton = findViewById(R.id.lock);
        mLockButton.setOnClickListener(this);
        mLoadingProgress = findViewById(R.id.loading);
    }

    /**
     * 快速添加各个组件
     * @param title 标题
     * @param isLive 是否为直播
     */
    public void addDefaultControlComponent(String title, boolean isLive) {
        CompleteView completeView = new CompleteView(getContext());
        ErrorView errorView = new ErrorView(getContext());
        PrepareView prepareView = new PrepareView(getContext());
        prepareView.setClickStart();
        TitleView titleView = new TitleView(getContext());
        titleView.setTitle(title);
        addControlComponent(completeView, errorView, prepareView, titleView);
        if (isLive) {
            addControlComponent(new LiveControlView(getContext()));
        } else {
            addControlComponent(new VodControlView(getContext()));
        }
        addControlComponent(new GestureView(getContext()));
        setCanChangePosition(!isLive);
    }

    @Override
    public void adjustView(int orientation, int space) {
        super.adjustView(orientation, space);
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            LayoutParams lblp = (LayoutParams) mLockButton.getLayoutParams();
            lblp.setMargins(dp24, 0, dp24, 0);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
            layoutParams.setMargins(dp24 + space, 0, dp24 + space, 0);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
            layoutParams.setMargins(dp24, 0, dp24, 0);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lock) {
            mMediaPlayerWrapper.toggleLockState();
        }
    }

    @Override
    public void onLock() {
        super.onLock();
        setGestureEnabled(false);
        mLockButton.setSelected(true);
        Toast.makeText(getContext(), R.string.video_player_locked, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnlock() {
        super.onUnlock();
        setGestureEnabled(true);
        mLockButton.setSelected(false);
        Toast.makeText(getContext(), R.string.video_player_unlocked, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void show(Animation showAnim) {
        super.show(showAnim);
        if (mMediaPlayerWrapper.isFullScreen()) {
            if (mLockButton.getVisibility() == GONE) {
                mLockButton.setVisibility(VISIBLE);
                if (showAnim != null) {
                    mLockButton.startAnimation(showAnim);
                }
            }
        }
    }

    @Override
    protected void hide(Animation hideAnim) {
        super.hide(hideAnim);
        if (mMediaPlayerWrapper.isFullScreen()) {
            mLockButton.setVisibility(GONE);
            if (hideAnim != null) {
                mLockButton.startAnimation(hideAnim);
            }
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case Constants.PLAYER_NORMAL:
                Log.e("StandardVideoController","PLAYER_NORMAL");
                setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mLockButton.setVisibility(GONE);
                break;
            case Constants.PLAYER_FULL_SCREEN:
                Log.e("StandardVideoController","PLAYER_FULL_SCREEN");
                if (isShowing()) {
                    mLockButton.setVisibility(VISIBLE);
                } else {
                    mLockButton.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            //调用release方法会回到此状态
            case Constants.STATE_IDLE:
                Log.e("","STATE_IDLE");
                mLockButton.setSelected(false);
                mLoadingProgress.setVisibility(GONE);
                break;
            case Constants.STATE_PLAYING:
                Log.e("","STATE_PLAYING");
                break;
            case Constants.STATE_PAUSED:
                Log.e("","STATE_PAUSED");
                break;
            case Constants.STATE_PREPARING:
                Log.e("","STATE_PREPARING");
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case Constants.STATE_PREPARED:
                Log.e("","STATE_PREPARED");
                mLoadingProgress.setVisibility(GONE);
                break;
            case Constants.STATE_ERROR:
                Log.e("","STATE_ERROR");
                mLoadingProgress.setVisibility(GONE);
                break;
            case Constants.STATE_BUFFERING:
                Log.e("","STATE_BUFFERING");
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case Constants.STATE_BUFFERED:
                Log.e("","STATE_BUFFERED");
                mLoadingProgress.setVisibility(GONE);
                break;
            case Constants.STATE_PLAYBACK_COMPLETED:
                Log.e("","STATE_PLAYBACK_COMPLETED");
                mLoadingProgress.setVisibility(GONE);
                mLockButton.setVisibility(GONE);
                mLockButton.setSelected(false);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isLocked()) {
            showInner();
            Toast.makeText(getContext(), R.string.video_player_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mMediaPlayerWrapper.isFullScreen()) {
            mMediaPlayerWrapper.stopFullScreen();
            return true;
        }
        return super.onBackPressed();
    }
}
