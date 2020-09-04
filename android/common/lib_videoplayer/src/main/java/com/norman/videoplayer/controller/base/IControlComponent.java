package com.norman.videoplayer.controller.base;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import com.norman.videoplayer.ui.ControlVideoView;

public interface IControlComponent {

    void attach(@NonNull ControlVideoView videoView);

    View getView();

    void show(Animation showAnim);

    void hide(Animation hideAnim);

    void onPlayStateChanged(int playState);

    void onPlayerStateChanged(int playerState);

    void adjustView(int orientation, int space);

    void setProgress(int duration, int position);

    void onLock();

    void onUnlock();

}
