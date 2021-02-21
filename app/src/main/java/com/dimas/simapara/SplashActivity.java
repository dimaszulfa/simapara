package com.dimas.simapara;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView image;
    ImageView image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bot_animation);

        image = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);

        image.setAnimation(topAnim);
        image2.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent utama = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(utama);
                finish();
            }
        },3000);
    }
}