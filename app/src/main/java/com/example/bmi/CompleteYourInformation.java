package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CompleteYourInformation extends AppCompatActivity {
    Button incWeight, decWeight, incLength, decLength, completeDataSaveButton;
    EditText weight, length, DOB;
    RadioGroup genderGroup;
    RadioButton genderButton;
    String uid;
    Calendar calendar;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_your_information);

        genderGroup = findViewById(R.id.radioGroup);
        genderButton = findViewById(R.id.radioMale);
        incWeight = findViewById(R.id.incWeight);
        weight = findViewById(R.id.weight);
        decWeight = findViewById(R.id.decWeight);
        incLength = findViewById(R.id.incLength);
        length = findViewById(R.id.length);
        decLength = findViewById(R.id.decLength);
        DOB = findViewById(R.id.DOB);
        uid = getIntent().getStringExtra("userId");
        completeDataSaveButton = findViewById(R.id.completeDataSaveButton);
        calendar = Calendar.getInstance();

        firestore = FirebaseFirestore.getInstance();

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                String myFormat="dd/MM/yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.UK);
                DOB.setText(dateFormat.format(calendar.getTime()));
            }
        };

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CompleteYourInformation.this,date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                genderButton = group.findViewById(checkedId);
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

        completeDataSaveButton.setOnClickListener(new View.OnClickListener() {
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
                if (DOB.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Length must bigger than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> infoObject = new HashMap<>();

                infoObject.put("Gender", genderButton.getText().toString());
                infoObject.put("Weight", weight.getText().toString());
                infoObject.put("Length", length.getText().toString());
                infoObject.put("DOB", DOB.getText().toString());

                int year = Integer.parseInt(DOB.getText().toString().substring(7));
                Double age = new Double(calendar.get(Calendar.YEAR) - year ) / 100;
                Double bmi_weight = Double.parseDouble(weight.getText().toString());
                Double bmi_length = Double.parseDouble(length.getText().toString());
                bmi_length = Math.pow(bmi_length / 100, 2);
                Double BMI;
                String BMI_Categorie = "";

                if (age >= 2 && age <= 10) {
                    BMI = (bmi_weight / bmi_length) * 0.7;
                } else if (age > 10 && age <= 20 && genderButton.getText().toString() == "Male") {
                    BMI = (bmi_weight / bmi_length) * 0.9;
                } else if (age > 10 && age <= 20 && genderButton.getText().toString() == "Female") {
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

                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);

                firestore.collection("Records").add(new Records(BMI_Categorie, formattedDate, "", uid, BMI, bmi_weight, Double.parseDouble(length.getText().toString())))
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                firestore.collection("Users").document(uid).update(infoObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent i = new Intent(CompleteYourInformation.this, Home.class);
                        i.putExtra("DOB", DOB.getText());
                        i.putExtra("gender", genderButton.getText());
                        startActivity(i);
                        finish();
                    }
                });
            }
        });
    }

}