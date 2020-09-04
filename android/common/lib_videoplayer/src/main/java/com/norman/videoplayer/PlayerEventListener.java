package com.norman.videoplayer;
/** 播放器事件回调 */
public interface PlayerEventListener {

    void onError(int code,String msg);

    void onCompletion();

    void onInfo(int what, int extra);

    void onPrepared();

    void onVideoSizeChanged(int width, int height);

}
