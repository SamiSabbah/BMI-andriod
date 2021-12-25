package com.example.bmi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private Context context;
    private List<Food> foodList;
    private static ClickListener listener;
    FirebaseFirestore firestore;

    public FoodAdapter(Context context, List<Food> results) {
        this.context = context;
        this.foodList = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();

        holder.foodListCat.setText(foodList.get(position).getFoodCat());
        holder.foodListCal.setText(foodList.get(position).getFoodCal());
        holder.foodListName.setText(foodList.get(position).getFoodName());
        Glide.with(context).load(foodList.get(position).getImageUri()).into(holder.foodListImage);
        int positions = position;

        holder.foodListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Foods").document(foodList.get(positions).getFoodId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();
                            foodList.remove(positions);
                            context.startActivity(new Intent(context, FoodList.class));
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodListImage;
        TextView foodListName, foodListCat, foodListCal;
        Button foodListEdit, foodListDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodListImage = itemView.findViewById(R.id.foodListImage);
            foodListName = itemView.findViewById(R.id.foodListName);
            foodListCat = itemView.findViewById(R.id.foodListCat);
            foodListCal = itemView.findViewById(R.id.foodListCal);
            foodListEdit = itemView.findViewById(R.id.foodListEdit);
            foodListDelete = itemView.findViewById(R.id.foodListDelete);

            foodListEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(foodList.get(getAdapterPosition()));
                }
            });

        }

    }

    public void OnItemClickListener(ClickListener listener) {
        FoodAdapter.listener = listener;
    }

    public interface ClickListener {
        void onClick(Food result);
    }
}