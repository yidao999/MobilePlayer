package com.example.startallplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void startAllPlayer(View v){
        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse("http://192.168.0.107:8080/aaa.mp4"),"video/*");
        intent.setDataAndType(Uri.parse("http://192.168.0.107:8080/ccc.rmvb"),"video/*");
        startActivity(intent);
    }
}
