package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewRecord extends AppCompatActivity {
    TextView backArrow, recordDate, time;
    Button incWeight, decWeight, incLength, decLength, saveDataButton;
    EditText weight, length;
    String DOB, gender;
    Calendar calendar;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        incWeight = findViewById(R.id.addRecordIncWeight);
        weight = findViewById(R.id.addRecordWeight);
        decWeight = findViewById(R.id.addRecordDecWeight);
        incLength = findViewById(R.id.addRecordIncLength);
        length = findViewById(R.id.addRecordLength);
        decLength = findViewById(R.id.addRecordDecLength);
        recordDate = findViewById(R.id.addRecordDate);
        time = findViewById(R.id.addRecordTime);
        calendar = Calendar.getInstance();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        saveDataButton = findViewById(R.id.saveDataButton);
        backArrow = findViewById(R.id.addRecordBackArrow);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
                recordDate.setText(dateFormat.format(calendar.getTime()));
            }
        };

        recordDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(NewRecord.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewRecord.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        decWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(weight.getText().toString());
                --count;
                weight.setText(Integer.toString(count));
            }
        });

        incWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View l) {
                int count = Integer.parseInt(weight.getText().toString());
                ++count;
                weight.setText(Integer.toString(count));
            }
        });

        decLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                int count = Integer.parseInt(length.getText().toString());
                --count;
                length.setText(Integer.toString(count));
            }
        });

        incLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                int count = Integer.parseInt(length.getText().toString());
                ++count;
                length.setText(Integer.toString(count));
            }
        });


        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(length.getText().toString()) <= 0) {
                    Toast.makeText(getApplicationContext(), "Length must bigger than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(weight.getText().toString()) <= 0) {
                    Toast.makeText(getApplicationContext(), "Weight must bigger than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (recordDate.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Length must bigger than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                firestore.collection("Users").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DOB = task.getResult().get("DOB").toString();
                            gender = task.getResult().get("Gender").toString();

                            int year = Integer.parseInt(DOB.substring(7));
                            Double age = new Double(calendar.get(Calendar.YEAR) - year) / 100;
                            Double bmi_weight = Double.parseDouble(weight.getText().toString());
                            Double bmi_length = Double.parseDouble(length.getText().toString());
                            bmi_length = Math.pow(bmi_length / 100, 2);
                            Double BMI;
                            String BMI_Categorie = "";

                            if (age >= 2 && age <= 10) {
                                BMI = (bmi_weight / bmi_length) * 0.7;
                            } else if (age > 10 && age <= 20 && gender == "Male") {
                                BMI = (bmi_weight / bmi_length) * 0.9;
                            } else if (age > 10 && age <= 20 && gender == "Female") {
                                BMI = (bmi_weight / bmi_length) * 0.9;
                            } else {
                                BMI = (bmi_weight / bmi_length);
                            }

                            if (BMI < 18.5) {
                                BMI_Categorie = "Underweight";
                            } else if (BMI >= 18.5 && BMI < 25) {
                                BMI_Categorie = "Healthy Weight";
                            } else if (BMI >= 25 && BMI < 30) {
                                BMI_Categorie = "Overweight";
                            } else if (BMI > 30) {
                                BMI_Categorie = "Obesity";
                            }

                            firestore.collection("Records").add(new Records(BMI_Categorie, recordDate.getText().toString(), "", auth.getUid(), BMI, bmi_weight, Double.parseDouble(length.getText().toString()))).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(NewRecord.this, Home.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}