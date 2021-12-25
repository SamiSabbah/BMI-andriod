package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Home extends AppCompatActivity {
    TextView userName, currentStatus, logout;
    Button addRecordButton, addFood, viewFoodButton;
    String userId, gender, weight, length, DOB;
    RecyclerView oldStatusRecycle;
    com.example.bmi.oldStatusAdapter oldStatusAdapter;
    List<Records> records;
    Calendar calendar;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userName = findViewById(R.id.userName);
        currentStatus = findViewById(R.id.currentStatus);
        logout = findViewById(R.id.logout);

        addRecordButton = findViewById(R.id.addRecordButton);
        addFood = findViewById(R.id.addFood);
        viewFoodButton = findViewById(R.id.viewFoodButton);

        oldStatusRecycle = findViewById(R.id.oldStatusRecycle);
        calendar = Calendar.getInstance();
        records = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userId = auth.getUid();

        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userName.setText("Hi, " + task.getResult().get("userName").toString());
                    gender = task.getResult().get("Gender").toString();
                    weight = task.getResult().get("Weight").toString();
                    length = task.getResult().get("Length").toString();
                    DOB = task.getResult().get("DOB").toString();


                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        firestore.collection("Records").whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        records.add(new Records(document.getData().get("bmi_Categories").toString(), document.getData().get("date").toString(), document.getData().get("time").toString(), userId, (double) document.getData().get("bmi"), (double) document.getData().get("weight"), (double) document.getData().get("length")));
                    }

                    Collections.sort(records);
                    Collections.reverse(records);

                    if (records.size() == 1) {
                        currentStatus.setText(records.get(0).getBMI_Categories());

                        oldStatusAdapter = new oldStatusAdapter(getApplicationContext(), records);
                        oldStatusRecycle.setHasFixedSize(true);
                        oldStatusRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        oldStatusRecycle.setAdapter(oldStatusAdapter);
                    } else if (records.size() > 1) {
                        currentStatus.setText(records.get(0).getBMI_Categories());
                        Double newBMI = records.get(0).getBMI();
                        Double oldBMI = records.get(1).getBMI();
                        String BMI_Cat = records.get(0).getBMI_Categories();

                        Double BMI_DIF = newBMI - oldBMI;

                        switch (BMI_Cat) {
                            case "Underweight":
                                if (BMI_DIF < -0.3) {
                                    currentStatus.setText(BMI_Cat + " (So Bad)");
                                } else if (BMI_DIF >= -0.3 && BMI_DIF < 0.3) {
                                    currentStatus.setText(BMI_Cat + " (Little Changes)");
                                } else if (BMI_DIF >= 0.3 && BMI_DIF < 0.6) {
                                    currentStatus.setText(BMI_Cat + " (Still Good)");
                                } else if (BMI_DIF >= 0.6 && BMI_DIF <= 1) {
                                    currentStatus.setText(BMI_Cat + " (Go Ahead)");
                                }
                                break;

                            case "Healthy Weight":
                                if (BMI_DIF < -1) {
                                    currentStatus.setText(BMI_Cat + " (So Bad)");
                                } else if (BMI_DIF >= -1 && BMI_DIF < -0.3) {
                                    currentStatus.setText(BMI_Cat + " (Be Careful)");
                                } else if (BMI_DIF >= -0.3 && BMI_DIF < 0.3) {
                                    currentStatus.setText(BMI_Cat + " (Little Changes)");
                                } else if (BMI_DIF >= 0.3 && BMI_DIF <= 1) {
                                    currentStatus.setText(BMI_Cat + " (Be Careful)");
                                }
                                break;

                            case "Overweight":
                                if (BMI_DIF < -1) {
                                    currentStatus.setText(BMI_Cat + " (Be Careful)");
                                } else if (BMI_DIF >= -1 && BMI_DIF < -0.6) {
                                    currentStatus.setText(BMI_Cat + " (Go Ahead)");
                                } else if (BMI_DIF >= -0.6 && BMI_DIF < -0.3) {
                                    currentStatus.setText(BMI_Cat + " (Still Good)");
                                } else if (BMI_DIF >= -0.3 && BMI_DIF < 0.3) {
                                    currentStatus.setText(BMI_Cat + " (Little Changes)");
                                } else if (BMI_DIF >= 0.3 && BMI_DIF < 0.6) {
                                    currentStatus.setText(BMI_Cat + " (Be Careful)");
                                } else if (BMI_DIF >= 0.6 && BMI_DIF <= 1) {
                                    currentStatus.setText(BMI_Cat + " (So Bad)");
                                }
                                break;

                            case "Obesity":
                                if (BMI_DIF < -0.6) {
                                    currentStatus.setText(BMI_Cat + " (Go Ahead)");
                                } else if (BMI_DIF >= -0.6 && BMI_DIF < 0) {
                                    currentStatus.setText(BMI_Cat + " (Little Changes)");
                                } else if (BMI_DIF >= 0 && BMI_DIF < 0.3) {
                                    currentStatus.setText(BMI_Cat + " (Be Careful)");
                                } else if (BMI_DIF >= 0.3 && BMI_DIF <= 1) {
                                    currentStatus.setText(BMI_Cat + " (So Bad)");
                                }
                                break;
                        }

                        oldStatusAdapter = new oldStatusAdapter(getApplicationContext(), records);
                        oldStatusRecycle.setHasFixedSize(true);
                        oldStatusRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        oldStatusRecycle.setAdapter(oldStatusAdapter);
                    }
                }
            }
        });


        addRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, NewRecord.class);
                startActivity(i);
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, AddFoodDetails.class);
                startActivity(i);
            }
        });

        viewFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, FoodList.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent i = new Intent(Home.this, Login.class);
                startActivity(i);
                finish();
            }
        });
    }
}