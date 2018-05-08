package com.example.cj.videoeditor;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.cj.videoeditor.widget.FocusImageView.TAG;

/**
 * Created by cj on 2017/6/26 .
 *
 */

public class Constants {
    public static InputStream mInputStream;
    public static String fileName;

    public static String getBaseFolder() {
        String baseFolder = Environment.getExternalStorageDirectory() + "/Codec/";
        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = MyApplication.getContext().getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }
    //获取VideoPath
    public static String getPath(String path, String fileName) {
        FileInputStream fileInputStream;
        String p = getBaseFolder() + path;
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder() + fileName;
        }
        try {
            fileInputStream = new FileInputStream(p+fileName);
            mInputStream = fileInputStream;
            Log.d(TAG, "getPath: finish inputstream");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileName = p+fileName;
        return p + fileName;
    }
}
