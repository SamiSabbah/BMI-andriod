package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddFoodDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView backArrow;
    EditText addFoodName, addFoodCalories;
    Spinner foodCategorySpinner;
    ImageView addFoodImageView;
    Button uploadPhoto, addFoodSave;
    Uri imageUri, uri;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    StorageReference storage;
    String foodCategory;
    int foodCategoryPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_details);

        backArrow = findViewById(R.id.backArrow);
        addFoodName = findViewById(R.id.addFoodName);
        addFoodCalories = findViewById(R.id.addFoodCalories);
        foodCategorySpinner = findViewById(R.id.foodCategorySpinner);
        addFoodImageView = findViewById(R.id.addFoodImageView);
        uploadPhoto = findViewById(R.id.uploadPhoto);
        addFoodSave = findViewById(R.id.addFoodSave);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 20);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_category, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        foodCategorySpinner.setAdapter(adapter);
        foodCategorySpinner.setOnItemSelectedListener(this);
        foodCategory = foodCategorySpinner.getSelectedItem().toString();

        addFoodSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFoodName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Food Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addFoodCalories.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Food Calories is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uri == null) {
                    Toast.makeText(getApplicationContext(), "Food Photo is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Food foodList = new Food();

                foodList.setFoodName(addFoodName.getText().toString());
                foodList.setFoodCat(foodCategoryPosition + "");
                foodList.setFoodCal(addFoodCalories.getText().toString());
                foodList.setImageUri(uri + "");
                foodList.setUserId(auth.getUid());

                firestore.collection("Foods").add(foodList).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            addFoodName.setText("");
                            addFoodCalories.setText("");
                            foodCategorySpinner.setSelection(0);
                            Glide.with(getApplicationContext()).load(R.drawable.ic_launcher_background).into(addFoodImageView);
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 20) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                Glide.with(getApplicationContext()).load(imageUri).into(addFoodImageView);
                ProgressDialog dialog = ProgressDialog.show(this, "Uploading..", "Please wait", true);
                final StorageReference reference = this.storage.child(auth.getUid());
                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                AddFoodDetails.this.uri = uri;
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        foodCategory = parent.getItemAtPosition(position).toString();
        foodCategoryPosition = parent.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}