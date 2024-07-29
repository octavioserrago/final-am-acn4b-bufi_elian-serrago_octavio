package com.project.nutritionintellij;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchMealActivity extends AppCompatActivity {

    private static final String TAG = "SearchMealActivity";

    private EditText etSearchQuery;
    private Button btnSearch;
    private LinearLayout llSearchResults;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Food> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_meal);

        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnSearch = findViewById(R.id.btnSearch);
        llSearchResults = findViewById(R.id.llSearchResults);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnSearch.setOnClickListener(v -> searchFood(etSearchQuery.getText().toString()));

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void searchFood(String query) {
        llSearchResults.removeAllViews();
        searchResults.clear();

        db.collection("foods")
                .whereEqualTo("name", query)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String foodName = document.getString("name");
                        String foodImageUrl = document.getString("imgUrl");
                        Double kcal = document.getDouble("kcals");
                        if (foodName != null && foodImageUrl != null && kcal != null) {
                            Food food = new Food(foodName, foodImageUrl, kcal);
                            searchResults.add(food);
                        }
                    }
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching for food: " + e.getMessage());
                    Toast.makeText(SearchMealActivity.this, "Error al buscar comida", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI() {
        llSearchResults.removeAllViews();
        for (Food food : searchResults) {
            LinearLayout resultLayout = new LinearLayout(SearchMealActivity.this);
            resultLayout.setOrientation(LinearLayout.HORIZONTAL);
            resultLayout.setPadding(16, 16, 16, 16);

            ImageView imageView = new ImageView(SearchMealActivity.this);
            Glide.with(SearchMealActivity.this)
                    .load(food.getImgUrl())
                    .into(imageView);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

            TextView textView = new TextView(SearchMealActivity.this);
            textView.setText(food.getName() + " - " + food.getKcals() + " kcal");
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            ));

            Button btnAddToFavorites = new Button(SearchMealActivity.this);
            btnAddToFavorites.setText("Añadir a favoritos");
            btnAddToFavorites.setOnClickListener(v -> addToFavorites(food));

            resultLayout.addView(imageView);
            resultLayout.addView(textView);
            resultLayout.addView(btnAddToFavorites);

            llSearchResults.addView(resultLayout);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) resultLayout.getLayoutParams();
            layoutParams.bottomMargin = 16;
            resultLayout.setLayoutParams(layoutParams);
        }
    }

    private void addToFavorites(Food food) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(uid);

            Map<String, Object> foodData = new HashMap<>();
            foodData.put("name", food.getName());
            foodData.put("kcals", food.getKcals());

            userRef.collection("favoriteFoods")
                    .document()
                    .set(foodData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(SearchMealActivity.this, food.getName() + " añadido a favoritos", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al añadir alimento favorito: " + e.getMessage());
                        Toast.makeText(SearchMealActivity.this, "Error al añadir alimento favorito", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(SearchMealActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}

