package com.boivalenko.java_swing_mp3player.utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

//Filter for choose files (only mp3 files) for FileChooser
public class FileFilterImpl extends FileFilter {

    private String fileExtension;
    private String fileDescription;

    public FileFilterImpl(String fileExtension, String fileDescription) {
        this.fileExtension = fileExtension;
        this.fileDescription = fileDescription;
    }

    //accept only folders und files with the filextension
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getAbsolutePath().endsWith(this.fileExtension);
    }

    //description for format extension by select dialog 
    @Override
    public String getDescription() {
        return this.fileDescription + " (*." + this.fileExtension + ")";
    }
}
