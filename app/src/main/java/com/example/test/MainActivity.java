package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         startFunc();
    }
    void startFunc(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
             Intent i=new Intent();
             i.setClass(getApplicationContext(),LoginActivity.class);
             i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
             i.putExtra("i",301);
             startActivity(i);
             finish();
            }
        }, 3000);

    }
}