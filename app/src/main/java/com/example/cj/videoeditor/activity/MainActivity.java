package com.example.cj.videoeditor.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj.videoeditor.R;



public class MainActivity extends BaseActivity implements View.OnClickListener  {


    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
     /*<uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
    <uses-permission  android:name="android.permission.INTERNET"/>*/
    private static final String permissons[] = {Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,Manifest.permission.INTERNET};

    private boolean debug = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化并发起权限申请
        //mPermissionHelper = new PermissionHelper(this, this);
        //mPermissionHelper.requestPermissions();

        if (hasPermission()) {
            setFragment();
        } else {
            requestPermission();
        }




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
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setFragment();
                } else {
                    requestPermission();
                }
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(int i=0;i<permissons.length;i++)
            {
                if(checkSelfPermission(permissons[i])==PackageManager.PERMISSION_GRANTED)
                    continue;
                else return false;
            }

            return true;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(int i=0;i<permissons.length;i++){
                if(shouldShowRequestPermissionRationale(permissons[i]))
                {
                    Toast.makeText(MainActivity.this, "Camera AND storage permission are required for this", Toast.LENGTH_LONG).show();
                    break;
                }
            }
            /*if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) || shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(MainActivity.this, "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
            }*/
            requestPermissions(permissons, PERMISSIONS_REQUEST);
        }
    }
    protected void setFragment() {
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
    }


}
