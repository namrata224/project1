package com.sunbeam.messenger.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sunbeam.messenger.R;
import com.sunbeam.messenger.utility.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                if (preferences.getBoolean(Constants.KEY_LOGIN_STATUS, false)) {
                    startActivity(new Intent(SplashActivity.this, FriendsListActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }

                finish();

            }
        }).start();
    }
}
