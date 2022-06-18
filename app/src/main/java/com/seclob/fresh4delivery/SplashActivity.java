package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(sharedPreferences.getString("phone", "").length()==10 && sharedPreferences.getString("id", "").length()>0)
                {
                    Intent intents = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intents);
                    finish();
                }else {

                    Intent intents = new Intent(SplashActivity.this, MobileActivity.class);
                    startActivity(intents);
                    finish();
                }
            }
        }, 2000);

    }
}