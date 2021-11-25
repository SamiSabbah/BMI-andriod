package com.example.bmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private TextView nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Splash.this, Login.class);
                startActivity(i);
                mHandler.removeCallbacksAndMessages(null);
                finish();
            }
        });

        mHandler.postDelayed(new Runnable() {
            public void run() {
                changeActivity();
            }
        }, 5000);

    }

    private void changeActivity() {
        Intent i = new Intent(Splash.this, Login.class);

        startActivity(i);
        finish();
    }
}
