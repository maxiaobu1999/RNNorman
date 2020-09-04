package com.norman.videoplayer;

public class VideoConfig {
    /** 渲染view类型，默认surface_view */
    public RenderType renderType = RenderType.RENDER_SURFACE_VIEW;
    /** 解码器类型 */
    public PlayerType playerType = PlayerType.PLAYER__IjkMediaPlayer;
    /** 是否循环播放 */
    public boolean isLoop=true;
    /** 使用什么渲染 */
    public enum RenderType {
        /** 使用SURFACE_VIEW渲染 SurfaceRenderView */
        RENDER_SURFACE_VIEW,
        /** 使用TEXTURE_VIEW渲染  */
        RENDER_TEXTURE_VIEW
    }

    public enum PlayerType {
        PLAYER__IjkExoMediaPlayer,
        PLAYER__AndroidMediaPlayer,
        PLAYER__IjkMediaPlayer
    }

}
