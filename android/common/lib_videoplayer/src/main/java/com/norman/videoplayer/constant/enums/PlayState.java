package com.norman.videoplayer.constant.enums;
/**
 * 姓名：malong on 2019/4/1 2:08 PM
 * 功能：播放状态
 */
public enum PlayState {
    STATE_NONE,//media_player未初始化或release(END状态)
    STATE_IDLE,//静止(media_player有，视频源未设置)
    STATE_INITIALIZED,//初始化状态（设置了data_source）
    STATE_PREPARING,//准备中
    STATE_PREPARED,//准备完成
    STATE_STARTED,//播放中
    STATE_PAUSED,//暂停
    STATE_STOPED,//停止播放
    STATE_PLAYBACK_COMPLETED,//播放结束
    STATE_ERROR//发生错误
}
