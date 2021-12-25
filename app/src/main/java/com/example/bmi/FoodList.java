package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {
    TextView foodListBackArrow;
    RecyclerView recyclerView;
    FoodAdapter foodAdapter;
    List<Food> foods;
    String[] foodCategory;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        foodListBackArrow = findViewById(R.id.foodListBackArrow);
        recyclerView = findViewById(R.id.foodListRecycle);

        foodCategory = getResources().getStringArray(R.array.food_category);

        foods = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        foodListBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firestore.collection("Foods").whereEqualTo("userId", auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String foodName = document.getData().get("foodName").toString();
                                String foodCal = document.getData().get("foodCal").toString();
                                String imageUri = document.getData().get("imageUri").toString();
                                int categoryFoodId = Integer.parseInt(document.getData().get("categoryFoodId").toString());
                                String category = foodCategory[categoryFoodId];
                                String foodDocumentId = document.getId();
                                foods.add(new Food(foodName, foodDocumentId, foodCal, category, auth.getUid(), imageUri, categoryFoodId));
                            }

                            foodAdapter = new FoodAdapter(getApplicationContext(), foods);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(foodAdapter);
                            foodAdapter.OnItemClickListener(new FoodAdapter.ClickListener() {
                                @Override
                                public void onClick(Food result) {
                                    Intent i = new Intent(getApplicationContext(), EditFoodDetails.class);
                                    i.putExtra("foodName", result.getFoodName());
                                    i.putExtra("foodCal", result.getFoodCal());
                                    i.putExtra("imageUri", result.getImageUri());
                                    i.putExtra("foodDocumentId", result.getFoodId());
                                    i.putExtra("category", result.getFoodCat());
                                    i.putExtra("categoryFoodId", result.getCategoryFoodId());
                                    startActivity(i);
                                }
                            });

                        }
                    }
                });
    }
}