package com.example.bmi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class oldStatusAdapter extends RecyclerView.Adapter<oldStatusAdapter.ViewHolder> {
    private List<Records> recordsList;
    private Context context;

    public oldStatusAdapter(Context context,  List<Records> results) {
        this.context=context;
        this.recordsList = results;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_status_items,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date_status.setText(recordsList.get(position).getDate());
        holder.weight_status.setText((recordsList.get(position).getWeight()).toString());
        holder.main_status.setText(recordsList.get(position).getBMI_Categories());
        holder.length_status.setText(recordsList.get(position).getLength().toString());
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView length_status, main_status, weight_status, date_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date_status = itemView.findViewById(R.id.date_status);
            weight_status = itemView.findViewById(R.id.weight_status);
            main_status = itemView.findViewById(R.id.main_status);
            length_status = itemView.findViewById(R.id.length_status);
        }
    }

}