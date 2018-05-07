package com.example.cj.videoeditor;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.cj.videoeditor.camera.SensorControler.TAG;

/**
 * Created by cj on 2017/6/26 .
 *
 */

public class Constants {

    public static FileInputStream fileInputStream;//fileInputStream

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
    //fileInputStream从路径创建
    public static String getPath(String path, String fileName) {
        String p = getBaseFolder() + path;
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder() + fileName;
        }
        try {
            fileInputStream = new FileInputStream(p+fileName);//
            Log.d(TAG, "getPath: finsh");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        
        return p + fileName;

    }
    //获取Inputstream 从fileInputStream妆花
    public static InputStream get_stream(){
        InputStream stream ;
        stream =  fileInputStream;
        
        if(stream!=null)
        {
            try {
                Log.d(TAG, "get_stream: finish, available = "+stream.available());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stream;
        }

        else {
            Log.d(TAG, "get_stream: false");
            return null;
        }
    }
}
