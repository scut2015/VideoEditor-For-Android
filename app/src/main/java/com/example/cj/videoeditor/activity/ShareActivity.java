package com.example.cj.videoeditor.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj.videoeditor.R;
import com.example.cj.videoeditor.fileserversdk.sdk.FileServerSDKTest;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ShareActivity extends AppCompatActivity {

    Handler handler=new Handler();
    FileServerSDKTest fileServerSDKTest=new FileServerSDKTest();
    ThreadPoolExecutor threadPool=new ThreadPoolExecutor(1,1,60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1),new ThreadPoolExecutor.DiscardPolicy());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Button buttonTest = (Button)findViewById(R.id.btn_test);
        buttonTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (fileServerSDKTest.runTest()){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ShareActivity.this,"云服务器可用,点击分享按钮",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ShareActivity.this,"云服务器不可用",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
