package com.example.cj.videoeditor.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cj.videoeditor.PermissionHelper;
import com.example.cj.videoeditor.PermissionInterface;
import com.example.cj.videoeditor.R;

public class MainActivity extends BaseActivity implements View.OnClickListener ,PermissionInterface {
    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置button透明度
        Button recordBtn = (Button) findViewById(R.id.record_activity);
        recordBtn.setOnClickListener(this);
        Button selectBtn = (Button) findViewById(R.id.select_activity);
        selectBtn.setOnClickListener(this);
        Button audioBtn = (Button) findViewById(R.id.audio_activity);
        audioBtn.setOnClickListener(this);
        recordBtn.getBackground().setAlpha(80);
        selectBtn.getBackground().setAlpha(80);
        audioBtn.getBackground().setAlpha(80);
        //设置textview字体
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/Pacifico-Regular.ttf");
        TextView txtshow=(TextView) findViewById(R.id.show);
        txtshow.setTypeface(typeFace);
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this,this);
        mPermissionHelper.requestPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_activity:
                startActivity(new Intent(MainActivity.this , RecordedActivity.class));
                break;
            case R.id.select_activity:
                startActivity(new Intent(MainActivity.this , VideoSelectActivity.class));
                break;
            case R.id.audio_activity:
                startActivity(new Intent(MainActivity.this , AudioEditorActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO

        };
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        finish();
    }

    private void initViews(){
        //已经拥有所需权限，可以放心操作任何东西了

    }

}
