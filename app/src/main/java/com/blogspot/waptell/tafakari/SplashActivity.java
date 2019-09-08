package com.blogspot.waptell.tafakari;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blogspot.waptell.tafakari.utils.SharedPreferenceConfig;

public class SplashActivity extends AppCompatActivity {

    //sharedpref
    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceConfig = new SharedPreferenceConfig(this);
        if (sharedPreferenceConfig.readWelcomeStatus()) {
            //start MainActivity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            // Start home activity
            startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            // close splash activity
            finish();
        }
    }
}