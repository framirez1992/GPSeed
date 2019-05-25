package com.far.gpseed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {
    Timer myTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        myTimer = new Timer();
        goToApp();
    }

    public void goToApp(){

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, Home.class));
                finish();
            }
        };
        myTimer.schedule(t,1000);

    }
}
