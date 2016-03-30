package com.tongji.tantairs.olderandroid;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by tantairs on 2015/9/7.
 */
public class FileUtil {
    private String SDPATH;

    public FileUtil() {
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    public String getSDPATH() {
        return SDPATH;
    }

    public File createSDFile(String fileName) {
        File file = new File(SDPATH + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
    }
}
