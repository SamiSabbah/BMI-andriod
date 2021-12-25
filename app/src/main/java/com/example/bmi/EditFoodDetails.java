package com.example.bmi;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditFoodDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView backArrow;
    EditText editFoodName, editFoodCalories;
    Spinner editFoodCategorySpinner;
    ImageView editFoodImageView;
    Button editFoodUploadPhoto, editFoodSave;
    Uri uri;
    Intent intent;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    StorageReference storage;
    String foodName, foodCal, imageUri, foodDocumentId, category;
    int categoryFoodId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_details);

        backArrow = findViewById(R.id.foodListBackArrow);

        backArrow = findViewById(R.id.editFoodBackArrow);
        editFoodName = findViewById(R.id.editFoodName);
        editFoodCalories = findViewById(R.id.editFoodCalories);
        editFoodCategorySpinner = findViewById(R.id.editFoodCategorySpinner);
        editFoodImageView = findViewById(R.id.editFoodImageView);
        editFoodUploadPhoto = findViewById(R.id.editFoodUploadPhoto);
        editFoodSave = findViewById(R.id.editFoodSave);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        foodName = intent.getStringExtra("foodName");
        foodCal = intent.getStringExtra("foodCal");
        imageUri = intent.getStringExtra("imageUri");
        foodDocumentId = intent.getStringExtra("foodDocumentId");
        category = intent.getStringExtra("category");
        categoryFoodId = intent.getIntExtra("categoryFoodId", 1);

        editFoodName.setText(foodName);
        editFoodCalories.setText(foodCal);
        Glide.with(getApplicationContext()).load(imageUri).into(editFoodImageView);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_category, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editFoodCategorySpinner.setAdapter(adapter);
        editFoodCategorySpinner.setSelection(categoryFoodId);

        editFoodCategorySpinner.setOnItemSelectedListener(this);

        editFoodUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 20);
            }
        });

        editFoodSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFoodName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Food Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editFoodCalories.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Food Calories is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imageUri == null) {
                    Toast.makeText(getApplicationContext(), "Food Photo is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> updatedFood = new HashMap<>();
                String foodName = editFoodName.getText().toString();
                String foodCalories = editFoodCalories.getText().toString();

                updatedFood.put("foodName", foodName);
                updatedFood.put("foodCal", foodCalories);
                updatedFood.put("imageUri", imageUri);
                updatedFood.put("categoryFoodId", categoryFoodId);
                firestore.collection("Foods").document(foodDocumentId)
                        .update(updatedFood).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), FoodList.class));
                        finish();
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
                uri = data.getData();
                ProgressDialog dialog = ProgressDialog.show(this, "Uploading...", "Please Wait", true);
                final StorageReference reference = storage.child(auth.getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getApplicationContext()).load(uri).into(editFoodImageView);
                                imageUri = uri.toString();
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
        category = parent.getItemAtPosition(position).toString();
        categoryFoodId = parent.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}