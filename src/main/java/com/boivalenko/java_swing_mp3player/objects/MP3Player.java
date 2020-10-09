package com.boivalenko.java_swing_mp3player.objects;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.boivalenko.java_swing_mp3player.gui.MP3PlayerGui;

public class MP3Player {

    //Help Class for Playing MP3 Files
    private BasicPlayer player = new BasicPlayer();

    private String currentFileName;
    private double currentVolumeValue;

    public MP3Player(MP3PlayerGui aThis) {
        player.addBasicPlayerListener(aThis);
    }


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
        }
        catch (BasicPlayerException bpEx) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    public void stop() {
        try {
            player.stop();
        }
        catch (BasicPlayerException bpEx) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    public void pause() {
        try {
            player.pause();
        }
        catch (BasicPlayerException bpEx) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    public void setVolume(int currentValue, int maximumValue) {
        try {
            this.currentVolumeValue = currentValue;

            if (currentValue == 0) {
                player.setGain(0);
            } else {
                player.setGain(calcVolume(currentValue, maximumValue));
            }

            //    player.setGain(currentVolumeValue); //set volume value
        }
        catch (BasicPlayerException bpEx) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, bpEx);
        }
    }

    //calculate necessary level of volume 
    public double calcVolume(int currentValue, int maximumValue) {
        this.currentVolumeValue = (double) currentValue / (double) maximumValue;
        return currentVolumeValue;
    }

    public void jump(long skipBytes) {
        try {
            player.seek(skipBytes);
            player.setGain(currentVolumeValue);
        }
        catch (BasicPlayerException bpEx) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, bpEx);
        }

    }


}
