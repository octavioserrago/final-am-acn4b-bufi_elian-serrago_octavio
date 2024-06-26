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

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Food> favoriteFoods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnAddNewMeal = findViewById(R.id.btnAddNewMeal);
        linearLayoutMeals = findViewById(R.id.linearLayoutMeals);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnAddNewMeal.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SearchMealActivity.class);
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
                        if (imgUrl != null) {
                            Food food = new Food(foodName, imgUrl);
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
        for (Food food : favoriteFoods) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(150, 150);
            imageView.setLayoutParams(imageParams);
            Glide.with(this)
                    .load(food.getImgUrl())
                    .into(imageView);

            TextView textView = new TextView(this);
            textView.setText(food.getName());

            itemLayout.addView(imageView);
            itemLayout.addView(textView);

            linearLayoutMeals.addView(itemLayout);
        }
    }
}
