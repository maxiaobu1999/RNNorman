package com.norman.videoplayer;

import com.norman.runtime.AppRuntime;

public class Constants {
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean DEBUG = AppRuntime.isDebug() & false;
    //播放器的各种状态
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;
    public static final int STATE_START_ABORT = 8;//开始播放中止

    // surface渲染方式
    /** 默认 */
    public static final int SCREEN_SCALE_DEFAULT = 0;
    /** 16:9 */
    public static final int SCREEN_SCALE_16_9 = 1;
    /** 4:3 */
    public static final int SCREEN_SCALE_4_3 = 2;
    /** 填充 */
    public static final int SCREEN_SCALE_MATCH_PARENT = 3;
    /** 原始大小 */
    public static final int SCREEN_SCALE_ORIGINAL = 4;
    /** 居中裁剪 */
    public static final int SCREEN_SCALE_CENTER_CROP = 5;

    //播放器状态
    /** 普通播放器 */
    public static final int PLAYER_NORMAL = 10;
    /** 全屏播放器 */
    public static final int PLAYER_FULL_SCREEN = 11;
    /** 小屏播放器 */
    public static final int PLAYER_TINY_SCREEN = 12;
}
