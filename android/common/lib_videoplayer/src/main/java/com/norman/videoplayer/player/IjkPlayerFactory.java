package com.norman.videoplayer.player;
/** ijkPlayer工厂 */
public class IjkPlayerFactory extends PlayerFactory<IjkPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkPlayer createPlayer() {
        return new IjkPlayer();
    }
}
