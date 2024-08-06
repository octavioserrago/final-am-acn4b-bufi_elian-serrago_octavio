package com.project.nutritionintellij;

import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<DocumentSnapshot> documentSnapshots;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public MealAdapter(Context context) {
        this.documentSnapshots = new ArrayList<>();
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void updateData(List<DocumentSnapshot> newSnapshots) {
        documentSnapshots.clear();
        documentSnapshots.addAll(newSnapshots);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        DocumentSnapshot snapshot = documentSnapshots.get(position);
        String mealName = snapshot.getString("name");
        String imageUrl = snapshot.getString("imgUrl");

        holder.tvMealName.setText(mealName);

        Glide.with(context)
                .load(imageUrl)
                .into(holder.ivMealImage);

        holder.btnAddToFavorites.setOnClickListener(v -> addToFavorites(snapshot));
    }

    @Override
    public int getItemCount() {
        return documentSnapshots.size();
    }

    private void addToFavorites(DocumentSnapshot snapshot) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            Map<String, Object> foodData = new HashMap<>();
            foodData.put("name", snapshot.getString("name"));
            foodData.put("kcals", snapshot.getDouble("kcals"));

            db.collection("users").document(uid)
                    .collection("favoriteFoods")
                    .document()
                    .set(foodData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Añadido a favoritos", Toast.LENGTH_SHORT).show();

                        ((Activity) context).setResult(Activity.RESULT_OK);
                        ((Activity) context).finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }


    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealName;
        ImageView ivMealImage;
        Button btnAddToFavorites;

        public MealViewHolder(View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            ivMealImage = itemView.findViewById(R.id.ivMealImage);
            btnAddToFavorites = itemView.findViewById(R.id.btnAddToFavorites);
        }
    }
}