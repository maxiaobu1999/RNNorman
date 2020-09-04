package com.norman.videoplayer;

/**
 * 视频播放器管理器，管理当前正在播放的VideoView，以及播放器配置
 */
public class VideoViewManager {

    /**
     * 当前正在播放的VideoView
     */
//    private List<MQLVideoView> mVideoViews = new ArrayList<>();

    /** 移动网络是否播放 */
    private boolean mPlayOnMobileNetwork;

    private VideoViewManager() {
        mPlayOnMobileNetwork = getConfig().mPlayOnMobileNetwork;
    }

    private static VideoViewManager sInstance;

    private static VideoViewConfig sConfig;

    public static void setConfig(VideoViewConfig config) {
        if (sConfig == null) {
            synchronized (VideoViewConfig.class) {
                if (sConfig == null) {
                    sConfig = config == null ? VideoViewConfig.newBuilder().build() : config;
                }
            }
        }
    }

    public static VideoViewConfig getConfig() {
        setConfig(null);
        return sConfig;
    }

    public boolean playOnMobileNetwork() {
        return mPlayOnMobileNetwork;
    }

    public void setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
        mPlayOnMobileNetwork = playOnMobileNetwork;
    }

    //
    public static VideoViewManager instance() {
        if (sInstance == null) {
            synchronized (VideoViewManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoViewManager();
                }
            }
        }
        return sInstance;
    }
//
//
//    public void addVideoView(MQLVideoView videoView) {
//        mVideoViews.add(videoView);
//    }
//
//    public void removeVideoView(MQLVideoView videoView) {
//        mVideoViews.remove(videoView);
//    }
//
//    public List<MQLVideoView> getVideoViews() {
//        return mVideoViews;
//    }
//
//    /**
//     * 获取最后一个添加到VideoViewManager的VideoView
//     * 一般来说此VideoView就是正在播放的那个
//     */
//    public MQLVideoView getLast() {
//        if (mVideoViews.size() > 0) {
//            return mVideoViews.get(mVideoViews.size() - 1);
//        } else {
//            return null;
//        }
//    }
//
//    public void pause() {
//        for (int i = 0; i < mVideoViews.size(); i++) {
//            MQLVideoView vv = mVideoViews.get(i);
//            if (vv != null) {
//                vv.pause();
//            }
//        }
//    }
//
//    public void resume() {
//        for (int i = 0; i < mVideoViews.size(); i++) {
//            MQLVideoView vv = mVideoViews.get(i);
//            if (vv != null) {
//                vv.resume();
//            }
//        }
//    }
//
//    public void release() {
//        for (int i = 0; i < mVideoViews.size(); i++) {
//            MQLVideoView vv = mVideoViews.get(i);
//            if (vv != null) {
//                vv.release();
//                i--;
//            }
//        }
//    }
//
//    public boolean onBackPressed() {
//        for (int i = 0; i < mVideoViews.size(); i++) {
//            MQLVideoView vv = mVideoViews.get(i);
//            if (vv != null) {
//                boolean b = vv.onBackPressed();
//                if (b) return true;
//            }
//        }
//        return false;
//    }
}
