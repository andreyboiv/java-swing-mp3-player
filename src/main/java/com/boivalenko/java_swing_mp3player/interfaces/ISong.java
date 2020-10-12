package com.boivalenko.java_swing_mp3player.interfaces;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public interface ISong extends BasicPlayerListener {

    void play(String fileName);

    void stop();

    void pause();

    void setVolume(int currentValue, int maximumValue);

    void jump(long skipBytes);

    default void setController(BasicController var1) {
    }

    void playSong();

    void rewindSongInPlayList();
}
