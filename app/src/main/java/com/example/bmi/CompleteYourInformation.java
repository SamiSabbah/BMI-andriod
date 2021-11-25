package com.example.bmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CompleteYourInformation extends AppCompatActivity {
    private Button completeDataSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_your_information);

        completeDataSaveButton = findViewById(R.id.completeDataSaveButton);

        completeDataSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CompleteYourInformation.this, Home.class);
                startActivity(i);
                finish();
            }
        });
    }
}