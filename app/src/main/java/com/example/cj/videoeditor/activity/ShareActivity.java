package com.example.cj.videoeditor.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj.videoeditor.Constants;
import com.example.cj.videoeditor.R;
import com.example.cj.videoeditor.fileserversdk.data.ResultDO;
import com.example.cj.videoeditor.fileserversdk.sdk.FileServerSDK;
import com.example.cj.videoeditor.fileserversdk.sdk.FileServerSDKTest;
import com.example.cj.videoeditor.fileserversdk.setting.Util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.cj.videoeditor.widget.FocusImageView.TAG;

public class ShareActivity extends BaseActivity implements View.OnClickListener {
    Handler handler = new Handler();
    FileServerSDKTest fileServerSDKTest = new FileServerSDKTest();
    FileServerSDK fileServerSDK = new FileServerSDK();
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardPolicy());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置textview字体
        setContentView(R.layout.activity_share);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Pacifico-Regular.ttf");
        TextView txtshow = (TextView) findViewById(R.id.show_share);
        txtshow.setTypeface(typeFace);

        //分享按钮
        Button buttonShare = (Button) findViewById(R.id.btn_share);
        buttonShare.getBackground().setAlpha(80);
        buttonShare.setOnClickListener(this);

        //测试按钮
        Button buttonTest = (Button) findViewById(R.id.btn_test);
        buttonTest.getBackground().setAlpha(80);
        buttonTest.setOnClickListener(this);
    }

    /**
     * TODO 在这个方法中获取视频的输入流
     *
     * @return
     */
    protected InputStream getInputStream() {
        FileInputStream fileInputStream;
        InputStream mInputStream = null;
        try {
            fileInputStream = new FileInputStream(Constants.mfileName);
            mInputStream = fileInputStream;
            Log.d(TAG, "finish inputstream");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mInputStream != null) {
            return mInputStream;
        } else {
            Toast.makeText(this, "请先对视频加滤镜再分享", Toast.LENGTH_SHORT).show();
            return null;
        }
        //ByteArrayInputStream bais = new ByteArrayInputStream("Congratulations".getBytes(Charset.forName("utf-8")));
        //return bais;
    }

    /**
     * TODO 在这个方法中获取文件名
     *
     * @return
     */
    protected String getFileName() {
        String fileName = Constants.mfileName;
        if (fileName != null && fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        }
        return fileName;
    }

    /**
     * TODO 在这个方法中,让用户去复制url
     */
    protected void showUrlFolUserToCopy(String url) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(url);
        Toast.makeText(this, "视频地址复制成功，请打开浏览器粘贴进行查看", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share: {
                //连接
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream = getInputStream();
                        String fileName = getFileName();
                        try {
                            if (inputStream == null || Util.isEmpty(fileName)) {
                                showToast("视频不存在");
                                return;
                            }
                            final String id = UUID.randomUUID().toString().substring(0, 8);
                            final String accessCode = UUID.randomUUID().toString().substring(0, 8);
                            ResultDO insertResult = fileServerSDK.insert(id, accessCode, fileName, inputStream);
                            if (!insertResult.getSuccess()) {
                                showToast("上传失败: " + insertResult.getMessage());
                                return;
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showUrlFolUserToCopy(fileServerSDK.getDisplay(id, accessCode));
                                }
                            });
                        } finally {
                            //关闭InpusStream
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                break;
            }
            case R.id.btn_test: {
                //测试网络连接是否正常,云服务器是否可用
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (fileServerSDKTest.runTest()) {
                                showToast("云服务器可用,点击分享按钮");
                            } else {
                                showToast("连接出错或云服务器不可用");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            }
        }
    }

    protected void showToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShareActivity.this, message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
