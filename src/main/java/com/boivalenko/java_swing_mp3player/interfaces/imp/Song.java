package com.boivalenko.java_swing_mp3player.interfaces.imp;

import com.boivalenko.java_swing_mp3player.gui.MP3PlayerGui;
import com.boivalenko.java_swing_mp3player.interfaces.ISong;
import com.boivalenko.java_swing_mp3player.objects.MP3;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import javax.swing.*;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Song implements ISong {

    //Help Class for Playing MP3 Files
    private BasicPlayer player = new BasicPlayer();

    private MP3PlayerGui jFrame;

    private String currentFileName;
    private double currentVolumeValue;

    private long secondsAmount; // how many seconds have passed since the start of playback
    private long duration; // song duration in seconds
    private int bytesLen; // song size in bytes
    private double posValue = 0.0; // scroll position
    private boolean movingFromJump = false;

    private boolean moveAutomatic = false; // while playing a song, the slider moves, moveAutomatic = true


    public Song(MP3PlayerGui jFrame) {
        this.jFrame = jFrame;
        player.addBasicPlayerListener(this);
    }

    @Override
    public void play(String fileName) {
        try {
            //When the same song should be continue played (after Pause)
            if (currentFileName != null && currentFileName.equals(fileName) && player.getStatus() == BasicPlayer.PAUSED) {
                player.resume();
                return;
            }

            this.currentFileName = fileName;
            player.open(new File(fileName));
            player.play();
            player.setGain(currentVolumeValue); //set volume value
        } catch (BasicPlayerException bpEx) {
            Logger.getLogger(Song.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    @Override
    public void stop() {
        try {
            player.stop();
        } catch (BasicPlayerException bpEx) {
            Logger.getLogger(Song.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    @Override
    public void pause() {
        try {
            player.pause();
        } catch (BasicPlayerException bpEx) {
            Logger.getLogger(Song.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    @Override
    public void setVolume(int currentValue, int maximumValue) {
        try {
            this.currentVolumeValue = currentValue;

            if (currentValue == 0) {
                player.setGain(0);
            } else {
                player.setGain(calcVolume(currentValue, maximumValue));
            }

        } catch (BasicPlayerException bpEx) {
            Logger.getLogger(Song.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    //calculate necessary level of volume 
    public double calcVolume(int currentValue, int maximumValue) {
        this.currentVolumeValue = (double) currentValue / (double) maximumValue;
        return currentVolumeValue;
    }

    @Override
    public void jump(long skipBytes) {
        try {
            player.seek(skipBytes);
            player.setGain(currentVolumeValue);
        } catch (BasicPlayerException bpEx) {
            Logger.getLogger(Song.class.getName()).log(Level.SEVERE, null, bpEx);
        }

    }

    @Override
    public void playSong() {
        int[] indexPlayList = this.jFrame.getsPlayList().getSelectedIndices();
        if (indexPlayList.length > 0) {
            ListModel mp3ListModel = jFrame.getsPlayList().getModel();
            MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[0]);
            play(mp3.getPath());
            this.jFrame.getjLabel1().setText(mp3.getName());
            setVolume(this.jFrame.getSlideVolume().getValue(), this.jFrame.getSlideVolume().getMaximum());
        }
    }


    @Override
    public void opened(Object o, Map properties) {
        //if song start
        duration = Math.round((((Long) properties.get("duration"))) / 1000000);
        bytesLen = (int) Math.round(((Integer) properties.get("mp3.length.bytes")));
    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] bytes, Map map) {

        float progress = -1.0f;

        if (bytesread > 0 && duration > 0) {
            progress = bytesread * 1.0f / bytesLen * 1.0f;
        }

        // how many seconds have passed since the start of playback
        secondsAmount = (long) (duration * progress);

        if (duration != 0) {
            if (movingFromJump == false) {
                this.jFrame.getSlideProgress().setValue(((int) Math.round(secondsAmount * 1000 / duration)));
            }
        }
    }

    @Override
    public void rewindSongInPlayList() {
        //how many bytes do we need to skip (jump)
        //If the user drags the slider then the value of 'slideProgress.getValueIsAdjusting()' will be true, else - false ...
        if (!jFrame.getSlideProgress().getValueIsAdjusting()) {
            if (moveAutomatic) {
                moveAutomatic = false;
                // scroll position
                posValue = jFrame.getSlideProgress().getValue() * 1.0 / 1000;
                processSeek(posValue);
            }
        } else {
            moveAutomatic = true;
            movingFromJump = true;
        }
    }

    private void processSeek(double bytes) {
        try {
            long skipBytes = Math.round((bytesLen) * bytes);
            jump(skipBytes);
        } catch (Exception e) {
            movingFromJump = false;
            e.printStackTrace();
        }
    }

    @Override
    public void stateUpdated(BasicPlayerEvent basicPlayerEvent) {
        int state = basicPlayerEvent.getCode();

        switch (state) {
            case BasicPlayerEvent.PLAYING:
                movingFromJump = false;
                break;
            case BasicPlayerEvent.SEEKING:
                movingFromJump = true;
                break;
            case BasicPlayerEvent.EOM:
                if (selectNextSong()) {
                    playSong();
                }
                break;
            default:
                break;
        }
    }


    private boolean selectNextSong() {
        int nextIndex = jFrame.getsPlayList().getSelectedIndex() + 1;
        if (nextIndex <= jFrame.getsPlayList().getModel().getSize() - 1) {
            jFrame.getsPlayList().setSelectedIndex(nextIndex);
            return true;
        }
        return false;
    }
}
