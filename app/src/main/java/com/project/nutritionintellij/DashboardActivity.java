package com.project.nutritionintellij;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private Button btnAddNewMeal;
    private LinearLayout linearLayoutMeals;
    private TextView textViewTotalCalories;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Food> favoriteFoods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnAddNewMeal = findViewById(R.id.btnAddNewMeal);
        Button btnCreateFood = findViewById(R.id.btnCreateFood);
        linearLayoutMeals = findViewById(R.id.linearLayoutMeals);
        textViewTotalCalories = findViewById(R.id.textViewTotalCalories);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnAddNewMeal.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SearchMealActivity.class);
            startActivity(intent);
        });

        btnCreateFood.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CreateFoodActivity.class);
            startActivity(intent);
        });

        loadFavoriteFoods();
    }


    private void loadFavoriteFoods() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            db.collection("users")
                    .document(uid)
                    .collection("favoriteFoods")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            showEmptyMessage();
                        } else {
                            favoriteFoods.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String foodName = document.getString("name");
                                if (foodName != null) {
                                    fetchFoodDetails(foodName);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading favorite foods: " + e.getMessage());
                        Toast.makeText(DashboardActivity.this, "Error loading favorite foods", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchFoodDetails(String foodName) {
        db.collection("foods")
                .whereEqualTo("name", foodName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String imgUrl = document.getString("imgUrl");
                        Double kcals = document.getDouble("kcals");
                        if (imgUrl != null) {
                            Food food = new Food(foodName, imgUrl, kcals);
                            favoriteFoods.add(food);
                        }
                    }
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching food details: " + e.getMessage());
                });
    }

    private void showEmptyMessage() {
        TextView textView = new TextView(this);
        textView.setText("Here you'll see your favorite foods, it's still empty.");
        linearLayoutMeals.addView(textView);
    }

    private void updateUI() {
        linearLayoutMeals.removeAllViews();
        double totalCalories = 0.0;

        for (Food food : favoriteFoods) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(150, 150);
            imageView.setLayoutParams(imageParams);
            Glide.with(this)
                    .load(food.getImgUrl())
                    .into(imageView);

            TextView nameTextView = new TextView(this);
            nameTextView.setText(food.getName());

            TextView kcalsTextView = new TextView(this);
            kcalsTextView.setText(String.format(" Calories: %.2f kcal", food.getKcals()));

            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> {
                deleteFoodFromFavorites(food.getName());
            });

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.setMargins(10, 0, 0, 0);

            itemLayout.addView(imageView);
            itemLayout.addView(nameTextView);
            itemLayout.addView(kcalsTextView);
            itemLayout.addView(deleteButton);

            linearLayoutMeals.addView(itemLayout);

            totalCalories += food.getKcals();
        }


        textViewTotalCalories.setText(String.format("Total Calories: %.2f", totalCalories));
    }

    private void deleteFoodFromFavorites(String foodName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users")
                    .document(uid)
                    .collection("favoriteFoods")
                    .whereEqualTo("name", foodName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(DashboardActivity.this, "Food removed", Toast.LENGTH_SHORT).show();
                                        favoriteFoods.removeIf(food -> food.getName().equals(foodName));
                                        updateUI();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error deleting food: " + e.getMessage());
                                        Toast.makeText(DashboardActivity.this, "Error deleting food", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error finding food to delete: " + e.getMessage());
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
