package com.boivalenko.java_swing_mp3player.interfaces;

import com.boivalenko.java_swing_mp3player.utils.FileFilterImpl;

import javax.swing.filechooser.FileFilter;
import java.awt.event.MouseEvent;

public interface IPlayList {

    String PLAYLIST_FILE_EXTENSION = "pls";

    String PLAYLIST_FILE_DESCRIPTION = "PLS Files";

    FileFilter PLAYLIST_FILE_FILTER = new FileFilterImpl(PLAYLIST_FILE_EXTENSION, PLAYLIST_FILE_DESCRIPTION);

    void openPlayList();

    void savePlayList();

    void searchInPlayList();

    void addSongsToPlayList();

    void deleteSongFromPlayList();

    //not play
    void goToNextIndexSongInPlayList();

    //not play
    void goToPrevIndexSongInPlayList();

    //play
    void playNextSongInPlayList();

    //play
    void playPrevSongInPlayList();

    void playSongInPlayList();

    void pauseSongInPlayList();

    void stopSongInPlayList();

    void changeSongVolumeInPlayList();

    void offSongVolumeInPlayList();

    void rewindSongInPlayList();

    void mouseClickPlayList(MouseEvent evt);

    void playListKeyPressed(java.awt.event.KeyEvent evt);

    void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt);
}
