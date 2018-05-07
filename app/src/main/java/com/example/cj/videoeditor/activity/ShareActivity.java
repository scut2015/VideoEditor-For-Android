package com.example.cj.videoeditor.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj.videoeditor.R;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Button button1 = (Button)findViewById(R.id.btn_share);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(ShareActivity.this,"已复制到剪切板，请打开浏览器进行查看",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
