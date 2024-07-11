package com.valdemar.spook;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    private static Typeface Pacifico;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //initFonts();
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("histories_notifications");
        FirebaseMessaging.getInstance().subscribeToTopic("chat");
        FirebaseMessaging.getInstance().subscribeToTopic("chats_notifications");
        initFonts();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    startActivity(new Intent(SplashActivity.this, ViewSpook.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, AccessRelato.class));
                    finish();
                }*/

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },1000);
    }



    private void initFonts() {
        this.Pacifico = Typeface.createFromAsset(getAssets(), "fuentes/Bloodlust.ttf");
        mTitle = findViewById(R.id.title);
        mTitle.setTypeface(Pacifico);
    }


}
