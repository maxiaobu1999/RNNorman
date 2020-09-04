package com.norman.videoplayer.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norman.runtime.AppRuntime;
import com.norman.videoplayer.constant.enums.PlayState;
import com.norman.videoplayer.render.IRenderView;
import com.norman.videoplayer.Constants;
import com.norman.videoplayer.PlayerEventListener;
import com.norman.videoplayer.VideoViewConfig;
import com.norman.videoplayer.VideoViewManager;
import com.norman.videoplayer.listener.OnPrepareListener;
import com.norman.videoplayer.player.AbstractPlayer;
import com.norman.videoplayer.player.PlayerFactory;

import static com.norman.videoplayer.constant.enums.PlayState.STATE_ERROR;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_IDLE;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_INITIALIZED;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_NONE;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_PAUSED;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_PLAYBACK_COMPLETED;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_PREPARED;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_PREPARING;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_STARTED;
import static com.norman.videoplayer.constant.enums.PlayState.STATE_STOPED;

/**
 * 播放器根类
 * <p>
 * 实现了播放器状态机 及常用回调
 * 基于Android MediaPlayer状态图设计
 * https://developer.android.com/reference/android/media/MediaPlayer.html#StateDiagram
 * <p>
 */
public class MQLVideoView extends FrameLayout implements PlayerEventListener {
    public static final String TAG = "MQLVideoView+++";
    private static boolean DEBUG = AppRuntime.isDebug() & true;
    /** 当前播放视频的地址 */
    protected String mUrl;

    /** 视频宽度 */
    private int mVideoWidth;
    /** 视频高度 */
    private int mVideoHeight;


    /** TextureView 或 SurfaceView */
    protected IRenderView mRenderView;
    /** 根容器，所用view放这上 */
    protected FrameLayout mPlayerContainer;
    /** surface容器 */
    protected FrameLayout mSurfaceContainer;
    /** controller控制器容器 */
    protected FrameLayout mControllerContainer;

    /** 视频控制器 */
    public AbstractPlayer mMediaPlayer;
    /** 当前播放状态 */
    public PlayState mCurrentState = STATE_NONE;

    /** 目标状态，eg：无论当前什么状态，暂停时则转换目标状态至STATE_PAUSED */

    /** 准备监听 */
    private OnPrepareListener mOnPrepareListener;

    public void setOnPrepareListener(OnPrepareListener onPrepareListener) {
        mOnPrepareListener = onPrepareListener;
    }

    public MQLVideoView(@NonNull Context context) {
        super(context);
        init();
    }

    public MQLVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MQLVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnErrorListener {
        void onError();
    }

    /** 发生错误时的监听 */
    private OnErrorListener mOnErrorListener;

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    /** 发生错误时的监听 */
    private OnCompleteListener mOnCompleteListener;

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        mOnCompleteListener = onCompleteListener;
    }

    /** 初始化 */
    public void init() {
        mCurrentState = STATE_NONE;
        // 创建surface容器 方便对surface操作
        mPlayerContainer = new FrameLayout(getContext());
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mSurfaceContainer = new FrameLayout(getContext());
        mSurfaceContainer.setBackgroundColor(Color.BLACK);
        mControllerContainer = new FrameLayout(getContext());
        mPlayerContainer.addView(mSurfaceContainer, params);
        mPlayerContainer.addView(mControllerContainer, params);
        this.addView(mPlayerContainer, params);
    }




    /*==============IVideoView BEGIN================*/

    /** 初始化MediaPlayer */
    public void initMediaPlayer() {
        VideoViewConfig config = VideoViewManager.getConfig();
        PlayerFactory playerFactory = config.mPlayerFactory;
        mMediaPlayer = playerFactory.createPlayer();
        mMediaPlayer.setPlayerEventListener(this);
//        setInitOptions();
        mMediaPlayer.initPlayer();
//        setOptions();
        //重建surface
        mCurrentState = STATE_IDLE;
    }


    /** 初始化视频渲染View */
    protected void addDisplay() {
        //删除旧的
        if (mRenderView != null) {
            if (mMediaPlayer != null) mMediaPlayer.setDisplay(null);
            mSurfaceContainer.removeView(mRenderView.getView());
            mRenderView.release();
            mRenderView = null;
        }

        //创建新的
        VideoViewConfig config = VideoViewManager.getConfig();
        IRenderView renderView = config.mRenderViewFactory.createRenderView(getContext());
        //初始化渲染层
        View renderUIView = renderView.getView();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        if (mVideoHeight != 0 || mVideoWidth != 0) {
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        }
        renderView.setAspectRatio(Constants.SCREEN_SCALE_CENTER_CROP);
        mSurfaceContainer.addView(renderUIView);
        mRenderView = renderView;
        mRenderView.attachToPlayer(mMediaPlayer);
    }

    /**
     * 设置 surface_view 宽高
     * STATE_STARTED 下依然生效
     */
    public void setVideoSize(int videoWidth, int videoHeight) {
        Log.d(TAG, "setVideoSize（）");
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        if (mRenderView != null) {
            mRenderView.setVideoSize(videoWidth, videoHeight);
        }
    }

    /**
     * 设置视频比例(填从方式)
     *
     * @param screenScaleType Constants.SCREEN_SCALE_16_9
     */
    public void setScreenScaleType(int screenScaleType) {
        if (mRenderView != null) {
            mRenderView.setAspectRatio(screenScaleType);
        }
    }

    //设置数据源
    protected void setDataSource(String url) {
        mUrl = url;
        if (mCurrentState != STATE_IDLE) {
            Log.e(TAG, "执行 setDataSource()状态异常，当前状态==" + mCurrentState.name()
                    + ",正常状态：STATE_IDLE");
            return;
        }
        mMediaPlayer.setDataSource(url, null);
        mCurrentState = STATE_INITIALIZED;
    }

    /** 播放前准备 */
    protected void prepare() {
        mMediaPlayer.prepare();
    }

    /**
     * 准备开始播放（异步）
     * STATE_INITIALIZED->STATE_PREPARING
     * renderView此时可能不可用，if（STATE_PREPARING）-》可用时再次执行prepare
     */
    protected void prepareAsync() {
        if (mCurrentState != STATE_INITIALIZED && mCurrentState != STATE_PREPARING && mCurrentState != STATE_STOPED) {
            Log.e(TAG, "执行 prepareAsync()状态异常，当前状态==" + mCurrentState.name()
                    + ",正常状态：STATE_INITIALIZED || STATE_PREPARING || STATE_STOPED");
            return;
        }
        mCurrentState = STATE_PREPARING;
//        if (null != mSurfaceHolder) {
//            //渲染层还未初始化完成，待完成后会再次调用 prepareAsync();
//            //绑定surface_view
//            mSurfaceHolder.attachToPlayer(mMediaPlayer);
//        } else {
//            return;
//        }
        mMediaPlayer.prepareAsync();
    }

    /**
     * 开始播放
     * STATE_PREPARED -> STATE_STARTED
     */
    protected void start() {
        if (mCurrentState != STATE_PREPARED && mCurrentState != STATE_PAUSED && mCurrentState != STATE_PLAYBACK_COMPLETED) {
            Log.e(TAG, "执行 start()状态异常，当前状态==" + mCurrentState.name()
                    + ",正常状态：STATE_PREPARED || STATE_PAUSED || STATE_PLAYBACK_COMPLETED");
            return;
        }
        mCurrentState = STATE_STARTED;
        mMediaPlayer.start();
    }

    /** 暂停播放 */
    protected void pause() {
        if (mCurrentState != STATE_STARTED) {
            Log.e(TAG, "执行 pause()状态异常，当前状态==" + mCurrentState.name()
                    + ",正常状态：STATE_STARTED");
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();//暂停播放
            mCurrentState = STATE_PAUSED;//设置当前播放状态
        }
    }

    /**
     * 停止播放
     */
    protected void stop() {
        if (mCurrentState != STATE_STARTED &&
                mCurrentState != STATE_PAUSED &&
                mCurrentState != STATE_PLAYBACK_COMPLETED) {
            Log.e(TAG, "执行 stop()状态异常，当前状态==" + mCurrentState.name()
                    + ",正常状态：STATE_STARTED  || STATE_PAUSED || STATE_PLAYBACK_COMPLETED");
            return;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mCurrentState = STATE_STOPED;
        }
    }

    /**
     * 调整播放进度
     *
     * @param time 毫秒
     */
    protected void seekTo(long time) {
        if (mCurrentState != STATE_PREPARED &&
                mCurrentState != STATE_STARTED &&
                mCurrentState != STATE_PAUSED &&
                mCurrentState != STATE_PLAYBACK_COMPLETED) {
            Log.e(TAG, "执行 seekTo()状态异常，当前状态==" + mCurrentState.name()
                    + ",正常状态：STATE_PREPARED  || STATE_STARTED || STATE_PAUSED || STATE_PLAYBACK_COMPLETED");
            return;
        }
        mMediaPlayer.seekTo(time);
    }

    /**
     * 重置media_player 可复用 更换视频源前使用
     * 清除回调
     * 任意状态（STATE_NONE除外） -> STATE_IDEL
     */
    protected void reset() {
        if (mCurrentState == STATE_NONE) {
            Log.e(TAG, "执行 reset()状态异常，当前状态==" + mCurrentState.name()
                    + ",正常状态：任意状态（STATE_NONE除外）");
            return;
        }
        mMediaPlayer.reset();
//        mUrl = null;
        mVideoWidth = 0;
        mVideoHeight = 0;
        mCurrentState = STATE_IDLE;
    }

    /**
     * 释放资源 退出不再播放时使用
     * MediaPlayer置null
     * 任意状态 -> STATE_NONE
     */
    protected void release() {
        mCurrentState = STATE_NONE;
        mVideoWidth = 0;
        mVideoHeight = 0;
        mUrl = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
//            IjkMediaPlayer.native_profileEnd();
        }


//            AudioManager am = (AudioManager) AppRuntime.getAppContext().getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
    }


    /*+++++++++++ PlayerEventListener播放器事件回调 BEGIN +++++++++++++*/
    @Override
    public void onError(int code, String msg) {
        mCurrentState = STATE_ERROR;
        Log.d(TAG, "onError():msg= " + msg);

    }

    /** 播放完了回调 */
    @Override
    public void onCompletion() {
        Log.d(TAG, "onCompletion() ");
        mCurrentState = STATE_PLAYBACK_COMPLETED;

    }

    @Override
    public void onInfo(int what, int extra) {
        if (DEBUG)
            Log.d(TAG, "onInfo():what=" + what + "-----extra=" + extra);

    }

    /**
     * 准备完成回调
     * STATE_PREPARING-> STATE_PREPARED
     */
    @Override
    public void onPrepared() {
        if (DEBUG)
            Log.d(TAG, "onPrepared() ");
        mCurrentState = STATE_PREPARED;
        if (mOnPrepareListener != null)
            mOnPrepareListener.onPrepared(MQLVideoView.this);
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        if (DEBUG)
            Log.d(TAG, "onVideoSizeChanged():width=" + width + "-----height=" + height);
        if (mRenderView != null) {
            mRenderView.setVideoSize(width, height);
        }
    }
    /*+++++++++++ PlayerEventListener播放器事件回调 END +++++++++++++*/

}
