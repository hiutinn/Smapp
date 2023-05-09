package com.hiutin.smapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(() -> {
            if (user == null) {
                startActivity(new Intent(SplashActivity.this, FragmentReplaceActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },3000);
    }
}