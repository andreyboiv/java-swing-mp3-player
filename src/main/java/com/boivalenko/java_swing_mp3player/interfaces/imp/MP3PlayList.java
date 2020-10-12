package com.boivalenko.java_swing_mp3player.interfaces.imp;

import com.boivalenko.java_swing_mp3player.gui.MP3PlayerGui;
import com.boivalenko.java_swing_mp3player.interfaces.IPlayList;
import com.boivalenko.java_swing_mp3player.interfaces.ISong;
import com.boivalenko.java_swing_mp3player.objects.MP3;
import com.boivalenko.java_swing_mp3player.utils.FileFilterImpl;
import com.boivalenko.java_swing_mp3player.utils.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;

public class MP3PlayList implements IPlayList {

    private static final String EMPTY_STRING = "";

    private final MP3PlayerGui jFrame;
    private final ISong song;

    private DefaultListModel mp3ListModel;

    private final String MP3_FILE_EXTENSION = "mp3";
    private final String MP3_FILE_DESCRIPTION = "MP3 Files";

    private final FileFilter mp3FileFilter = new FileFilterImpl(MP3_FILE_EXTENSION, MP3_FILE_DESCRIPTION);

    private int currentValue;


    public MP3PlayList(MP3PlayerGui jFrame) {
        this.jFrame = jFrame;
        this.mp3ListModel = new DefaultListModel();
        this.song = new Song(jFrame);

        jFrame.getsPlayList().setModel(mp3ListModel);
        jFrame.setLocationRelativeTo(null);
    }

    @Override
    public void openPlayList() {
        JFileChooser fileChooser = jFrame.getFileChooser();
        FileUtils.addFileFilter(fileChooser, PLAYLIST_FILE_FILTER);
        int result = fileChooser.showOpenDialog(this.jFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            DefaultListModel obj = (DefaultListModel) FileUtils.deserialize(file.getPath());
            this.mp3ListModel = obj;
            jFrame.getsPlayList().setModel(obj);
        }
    }

    @Override
    public void savePlayList() {
        if (jFrame.getsPlayList().getModel().getSize() > 0) {
            JFileChooser fileChooser = jFrame.getFileChooser();
            FileUtils.addFileFilter(fileChooser, PLAYLIST_FILE_FILTER);
            int result = fileChooser.showSaveDialog(this.jFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.exists()) {
                    int resultOvveride = JOptionPane.showConfirmDialog(this.jFrame, "File already exists. Would you still overwrite it? ", "", JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (resultOvveride) {
                        case JOptionPane.NO_OPTION:
                            savePlayList();// open the method recursive...
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            fileChooser.cancelSelection();
                            return;
                    }
                    fileChooser.approveSelection();
                }
                String fileExtension = FileUtils.getFileExtension(selectedFile);
                String fileNameForSave = (fileExtension != null && fileExtension.equals(PLAYLIST_FILE_EXTENSION)) ? selectedFile.getPath() :
                        selectedFile.getPath() + "." + PLAYLIST_FILE_EXTENSION;
                FileUtils.serialize(mp3ListModel, fileNameForSave);
            }
        }
    }

    @Override
    public void searchInPlayList() {
        String searchStr = this.jFrame.getTextfSearch().getText();

        if (searchStr == null || searchStr.trim().equals(EMPTY_STRING) || this.jFrame.getsPlayList().getModel().getSize() == 0) {
            return;
        }

        ArrayList<Integer> mp3FindedIndexes = new ArrayList<>();

        for (int i = 0; i < mp3ListModel.size(); i++) {
            MP3 mp3 = (MP3) mp3ListModel.getElementAt(i);
            if (FileUtils.getFileNameWithoutExtension(mp3.getPath()).toUpperCase().contains(searchStr.toUpperCase())) {
                mp3FindedIndexes.add(i);
            }
        }

        if (mp3FindedIndexes.isEmpty()) {
            JOptionPane.showMessageDialog(this.jFrame, "The search \'" + searchStr + "\' returned no results");
            this.jFrame.getTextfSearch().requestFocus();
            this.jFrame.getTextfSearch().selectAll();
            this.jFrame.getsPlayList().clearSelection();
            return;
        }

        int[] selectIndexes = new int[mp3FindedIndexes.size()];

        for (int i = 0; i < selectIndexes.length; i++) {
            selectIndexes[i] = mp3FindedIndexes.get(i);
        }

        this.jFrame.getsPlayList().setSelectedIndices(selectIndexes);
    }

    @Override
    public void addSongsToPlayList() {
        JFileChooser fileChooser = jFrame.getFileChooser();

        FileUtils.addFileFilter(fileChooser, mp3FileFilter); //set file filter
        int result = fileChooser.showOpenDialog(this.jFrame); //stored result (checked file or not)

        //if presses OK or Yes
        if (result == JFileChooser.APPROVE_OPTION) {
            //we can choose many songs
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                MP3 mp3 = new MP3(file.getName(), file.getPath());
                if (!mp3ListModel.contains(mp3)) {
                    mp3ListModel.addElement(mp3);
                } else {
                    JOptionPane.showMessageDialog(this.jFrame, "The file (song/video)  \'" + file.getName() + "\' is already in the playlist");
                }
            }
        }
    }

    @Override
    public void deleteSongFromPlayList() {
        int[] indexPlayList = jFrame.getsPlayList().getSelectedIndices();
        if (indexPlayList.length > 0) {
            ArrayList<MP3> mp3ListForRemove = new ArrayList<>();
            for (int i = 0; i < indexPlayList.length; i++) {
                MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[i]);
                mp3ListForRemove.add(mp3);
            }
            for (MP3 mp3 : mp3ListForRemove) {
                mp3ListModel.removeElement(mp3);
            }
        }
    }

    @Override
    public void goToNextIndexSongInPlayList() {
        int nextIndex = this.jFrame.getsPlayList().getSelectedIndex() + 1;
        if (nextIndex <= mp3ListModel.getSize() - 1) {
            this.jFrame.getsPlayList().setSelectedIndex(nextIndex);
        }
    }

    @Override
    public void goToPrevIndexSongInPlayList() {
        int nextIndex = this.jFrame.getsPlayList().getSelectedIndex() - 1;
        if (nextIndex >= 0) {
            this.jFrame.getsPlayList().setSelectedIndex(nextIndex);
        }
    }

    @Override
    public void playNextSongInPlayList() {
        goToNextIndexSongInPlayList();
        playSongInPlayList();
    }

    @Override
    public void playPrevSongInPlayList() {
        goToPrevIndexSongInPlayList();
        playSongInPlayList();
    }

    @Override
    public void playSongInPlayList() {
        song.playSong();
    }

    @Override
    public void pauseSongInPlayList() {
        this.song.pause();
    }

    @Override
    public void stopSongInPlayList() {
        this.song.stop();
    }

    @Override
    public void changeSongVolumeInPlayList() {
        this.song.setVolume(this.jFrame.getSlideVolume().getValue(), this.jFrame.getSlideVolume().getMaximum());

        if (this.jFrame.getSlideVolume().getValue() == 0) {
            this.jFrame.getTglbtnVolume().setSelected(true);
        } else {
            this.jFrame.getTglbtnVolume().setSelected(false);
        }
    }

    @Override
    public void offSongVolumeInPlayList() {
        if (this.jFrame.getTglbtnVolume().isSelected()) {
            this.currentValue = this.jFrame.getSlideVolume().getValue();
            this.jFrame.getSlideVolume().setValue(0);
        } else {
            this.jFrame.getSlideVolume().setValue(currentValue);
        }
    }

    @Override
    public void rewindSongInPlayList() {
        song.rewindSongInPlayList();
    }

    @Override
    public void mouseClickPlayList(MouseEvent evt) {
        if (evt.getModifiers() == InputEvent.BUTTON1_MASK && evt.getClickCount() == 2) {
            int[] indexPlayList = this.jFrame.getsPlayList().getSelectedIndices();
            if (indexPlayList.length > 0) {
                MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[0]);
                song.play(mp3.getPath());
                this.jFrame.getjLabel1().setText(mp3.getName());
                song.setVolume(this.jFrame.getSlideVolume().getValue(), this.jFrame.getSlideVolume().getMaximum());
            }
        }
    }

    @Override
    public void playListKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int[] indexPlayList = this.jFrame.getsPlayList().getSelectedIndices();
            if (indexPlayList.length > 0) {
                MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[0]);
                song.play(mp3.getPath());
                this.jFrame.getjLabel1().setText(mp3.getName());
                song.setVolume(this.jFrame.getSlideVolume().getValue(), this.jFrame.getSlideVolume().getMaximum());
            }
        }
    }

    @Override
    public void formMouseWheelMoved(MouseWheelEvent evt) {

        int notches = evt.getWheelRotation();

        if (notches < 0) {
            this.jFrame.getSlideVolume().setValue(this.jFrame.getSlideVolume().getValue() + 10);
        } else if (notches > 0) {
            this.jFrame.getSlideVolume().setValue(this.jFrame.getSlideVolume().getValue() - 10);
        }

        this.song.setVolume(this.jFrame.getSlideVolume().getValue(), this.jFrame.getSlideVolume().getMaximum());

    }
}
