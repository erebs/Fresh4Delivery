package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        getSupportActionBar().hide();
        startTimer();
    }

    public void CreatActivuty(View view)
    {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    void startTimer() {
        TextView tv;
        tv = (TextView) findViewById(R.id.timer);
        CountDownTimer cTimer;
        cTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv.setText("you will be redirected to home screen\nwithin " +String.valueOf(millisUntilFinished/1000)+" seconds.");
            }
            public void onFinish() {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        };
        cTimer.start();
    }

}