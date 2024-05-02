package com.example.gpstrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, R.anim.fade_out);
                }
            }
        };

        thread.start();


    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread2 = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, R.anim.fade_out);
                }
            }
        };

        thread2.start();
    }
}