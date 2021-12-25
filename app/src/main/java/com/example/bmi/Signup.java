package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    TextView loginButton;
    Button createAccountButton;
    TextInputEditText name, email, password, repassword;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        name = findViewById(R.id.name);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        repassword = findViewById(R.id.signupRe_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this, Login.class);
                startActivity(i);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "E-Mail is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().equals("") || password.getText().toString().length() < 7) {
                    Toast.makeText(getApplicationContext(), "The password must be at least 7 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!repassword.getText().toString().equals(password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DocumentReference doc = firestore.collection("Users").document(auth.getUid());
                            Map<String, Object> user = new HashMap<>();
                            user.put("userName", name.getText().toString());
                            doc.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent i = new Intent(Signup.this, CompleteYourInformation.class);
                                    i.putExtra("userId", auth.getUid());
                                    startActivity(i);
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            Log.d("sami-test", task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }
}