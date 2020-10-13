package com.boivalenko.java_swing_mp3player.utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileUtils {

    public static String getFileNameWithoutExtension(String fileName) {
        File file = new File(fileName);
        int index = file.getName().lastIndexOf('.');
        if (index > 0 && index <= file.getName().length() - 2) {
            return file.getName().substring(0, index);
        }
        return "noname";
    }

    public static String getFileExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    //delete current file filter and ser new file filter
    public static void addFileFilter(JFileChooser jfc, FileFilter aff) {
        jfc.removeChoosableFileFilter(jfc.getFileFilter());
        jfc.setFileFilter(aff);
        jfc.setSelectedFile(new File("")); //delete last name from opened/saved file
    }


    //save object
    public static void serialize(Object obj, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(obj);
        } catch (Exception e) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //open object
    public static Object deserialize(String filename) {
        Object retVal = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            retVal = ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return retVal;
    }
}