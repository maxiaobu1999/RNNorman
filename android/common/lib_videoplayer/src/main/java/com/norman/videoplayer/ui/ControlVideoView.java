package com.norman.videoplayer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.videoplayer.Constants;
import com.norman.videoplayer.controller.base.BaseVideoController;
import com.norman.videoplayer.listener.OnPrepareListener;
import com.norman.videoplayer.listener.OnVideoViewStateChangeListener;
import com.norman.videoplayer.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class ControlVideoView extends BaseVideoView {
    protected static final int FULLSCREEN_FLAGS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    /** 是否处于全屏状态 */
    protected boolean mIsFullScreen;
    /** 播放器状态：普通、全屏、小屏 */
    protected int mCurrentPlayerState = Constants.PLAYER_NORMAL;
    /** 当前播放器的状态 */
    protected int mCurrentPlayState = Constants.STATE_IDLE;
    /** OnVideoViewStateChangeListener集合，保存了所有开发者设置的监听器*/
    protected List<OnVideoViewStateChangeListener> mOnVideoViewStateChangeListeners;
    /** 通过添加和移除这个view来实现隐藏和显示navigation bar，可以避免出现一些奇奇怪怪的问题 */
    @Nullable
    protected View mHideNavBarView;
    /** 控制器 */
    @Nullable
    protected BaseVideoController mVideoController;

    public ControlVideoView(@NonNull Context context) {
        super(context);
    }

    public ControlVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** 切换锁定状态 */
    public void toggleLockState() {
        if (mVideoController != null) {
            mVideoController.setLocked(!mVideoController.isLocked());
        }
    }


    /** 设置控制器，传null表示移除控制器 */
    public void setVideoController(@Nullable BaseVideoController mediaController) {
        mControllerContainer.removeView(mVideoController);
        mVideoController = mediaController;
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mControllerContainer.addView(mVideoController, params);
        }
    }

    /** 返回键点击 */
    public boolean onBackPressed() {
        return mVideoController != null && mVideoController.onBackPressed();
    }

    /** 控制器正在显示 */
    public boolean isShowing() {
        return mVideoController != null && mVideoController.isShowing();
    }


    /** 切换显示/隐藏状态 */
    public void toggleShowState() {
        if (mVideoController == null) {
            return;
        }
        if (mVideoController.isShowing()) {
//            mVideoController.hideInner();
            hideInner();
        } else {
//            mVideoController.showInner();
            showInner();
        }
    }

    /** 隐藏播放视图 */
    public void hideInner() {
        if (mVideoController == null) {
            return;
        }
        mVideoController.hideInner();
    }

    /** 显示播放视图 */
    public void showInner() {
        if (mVideoController == null) {
            return;
        }
        mVideoController.showInner();
    }

    /** 横竖屏切换 */
    public void toggleFullScreen() {
        if (isFullScreen()) {
            stopFullScreen();
        } else {
            startFullScreen();
        }
    }

    /** 当前是否全屏 */
    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    public void startFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onStartFullScreen();
    }

    public void stopFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        onStopFullScreen();
    }

    /** 进入全屏 setRequestedOrientation()后调用 */
    public void onStartFullScreen() {
        if (mIsFullScreen)
            return;

        ViewGroup decorView = getDecorView();
        if (decorView == null)
            return;

        mIsFullScreen = true;

        //隐藏NavigationBar和StatusBar
        if (mHideNavBarView == null) {
            mHideNavBarView = new View(getContext());
        }
        mHideNavBarView.setSystemUiVisibility(FULLSCREEN_FLAGS);
        mPlayerContainer.addView(mHideNavBarView);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //从当前FrameLayout中移除播放器视图
        this.removeView(mPlayerContainer);
        //将播放器视图添加到DecorView中即实现了全屏
        decorView.addView(mPlayerContainer);
        setPlayerState(Constants.PLAYER_FULL_SCREEN);

    }

    /**
     * 退出全屏
     * setRequestedOrientation()后调用
     */
    public void onStopFullScreen() {
        if (!mIsFullScreen)
            return;

        ViewGroup decorView = getDecorView();
        if (decorView == null)
            return;

        mIsFullScreen = false;
        //显示NavigationBar和StatusBar
        mPlayerContainer.removeView(mHideNavBarView);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //把播放器视图从DecorView中移除并添加到当前FrameLayout中即退出了全屏
        decorView.removeView(mPlayerContainer);
        this.addView(mPlayerContainer);

        setPlayerState(Constants.PLAYER_NORMAL);
    }


    /** 获取DecorView */
    protected ViewGroup getDecorView() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    /** 获取Activity */
    protected Activity getActivity() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
//        if (activity == null) {
//            if (mVideoController == null) return null;
//            activity = PlayerUtils.scanForActivity(mVideoController.getContext());
//        }
        return activity;
    }


    /** 向Controller设置播放状态，用于控制Controller的ui展示 */
    protected void setPlayState(int playState) {
        mCurrentPlayState = playState;
        if (mVideoController != null)
            mVideoController.setPlayState(playState);
        if (mOnVideoViewStateChangeListeners != null) {
            for (int i = 0, z = mOnVideoViewStateChangeListeners.size(); i < z; i++) {
                OnVideoViewStateChangeListener listener = mOnVideoViewStateChangeListeners.get(i);
                if (listener != null) {
                    listener.onPlayStateChanged(playState);
                }
            }
        }
    }

    /** 向Controller设置播放器状态，包含全屏状态和非全屏状态 */
    protected void setPlayerState(int playerState) {
        mCurrentPlayerState = playerState;
        if (mVideoController != null)
            mVideoController.setPlayerState(playerState);
        if (mOnVideoViewStateChangeListeners != null) {
            for (int i = 0, z = mOnVideoViewStateChangeListeners.size(); i < z; i++) {
                OnVideoViewStateChangeListener listener = mOnVideoViewStateChangeListeners.get(i);
                if (listener != null) {
                    listener.onPlayerStateChanged(playerState);
                }
            }
        }
    }

    /** 开始刷新进度 */
    public void startProgress() {
        if (mVideoController != null)
            mVideoController.startProgress();
    }

    /** 开始计时 */
    public void startFadeOut() {
        //重新开始计时
        if (mVideoController != null)
            mVideoController.startFadeOut();
    }

    /** 取消计时 */
    public void stopFadeOut() {
        if (mVideoController != null)
            mVideoController.startFadeOut();
    }


    /** 停止刷新进度 */
    public void stopProgress() {
        if (mVideoController != null)
            mVideoController.stopProgress();
    }


    /** 监听播放状态变化 */
    public void addOnVideoViewStateChangeListener(@NonNull OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners == null) {
            mOnVideoViewStateChangeListeners = new ArrayList<>();
        }
        mOnVideoViewStateChangeListeners.add(listener);
    }

    /** 移除播放状态监听 */
    public void removeOnVideoViewStateChangeListener(@NonNull OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners != null) {
            mOnVideoViewStateChangeListeners.remove(listener);
        }
    }

    /** 设置播放状态监听 */
    public void setOnVideoViewStateChangeListener(@NonNull OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners == null) {
            mOnVideoViewStateChangeListeners = new ArrayList<>();
        } else {
            mOnVideoViewStateChangeListeners.clear();
        }
        mOnVideoViewStateChangeListeners.add(listener);
    }

    /** 移除所有播放状态监听 */
    public void clearOnVideoViewStateChangeListeners() {
        if (mOnVideoViewStateChangeListeners != null) {
            mOnVideoViewStateChangeListeners.clear();
        }
    }

    public void initialization() {
        initMediaPlayer();
        addDisplay();
    }

    @Override
    public void setDataSource(String url) {
        super.setDataSource(url);
    }

    @Override
    public void startOnPrepared() {
        super.startOnPrepared();
    }

    @Override
    public void play(String url) {
        if (TextUtils.isEmpty(url)) {
            url = mUrl;
        }
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

    @Override
    protected void prepareAsync() {
        super.prepareAsync();
        setPlayState(Constants.STATE_PREPARING);

    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        setPlayState(Constants.STATE_PREPARED);
    }

    @Override
    public void start() {
        super.start();
        setPlayState(Constants.STATE_PLAYING);

    }

    @Override
    public void pause() {
        super.pause();
        setPlayState(Constants.STATE_PAUSED);
    }

    @Override
    public void release() {
        super.release();
        setPlayState(Constants.STATE_IDLE);

    }

    @Override
    public void seekTo(long time) {
        super.seekTo(time);
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        setPlayState(Constants.STATE_PLAYBACK_COMPLETED);
    }

    @Override
    public void reset() {
        super.reset();
        setPlayState(Constants.STATE_IDLE);
    }

    @Override
    public void onError(int code, String msg) {
        super.onError(code, msg);
        setPlayState(Constants.STATE_ERROR);
    }


}
