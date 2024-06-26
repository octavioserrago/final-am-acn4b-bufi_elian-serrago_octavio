package com.project.nutritionintellij;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<DocumentSnapshot> documentSnapshots;

    public MealAdapter() {
        this.documentSnapshots = new ArrayList<>();
    }

    public void updateData(List<DocumentSnapshot> newSnapshots) {
        documentSnapshots.clear();
        documentSnapshots.addAll(newSnapshots);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        DocumentSnapshot snapshot = documentSnapshots.get(position);
        String mealName = snapshot.getString("name");
        holder.tvMealName.setText(mealName);
    }

    @Override
    public int getItemCount() {
        return documentSnapshots.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealName;

        public MealViewHolder(View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tvMealName);
        }
    }
}
