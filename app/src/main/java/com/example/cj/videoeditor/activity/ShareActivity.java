package com.example.cj.videoeditor.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj.videoeditor.R;

public class ShareActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置textview字体
        setContentView(R.layout.activity_share);
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/Pacifico-Regular.ttf");
        TextView txtshow=(TextView) findViewById(R.id.show_share);
        txtshow.setTypeface(typeFace);

        Button shareBtn = (Button)findViewById(R.id.btn_share);
        //设置button透明度
        shareBtn.getBackground().setAlpha(80);

        shareBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(ShareActivity.this,"已复制到剪切板，请打开浏览器进行查看",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
