package com.project.nutritionintellij;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private List<String> searchResults = new ArrayList<>();

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
                        if (foodName != null) {
                            searchResults.add(foodName);
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
        for (String foodName : searchResults) {
            TextView textView = new TextView(SearchMealActivity.this);
            textView.setText(foodName);
            textView.setPadding(8, 8, 8, 8);

            Button btnAddToFavorites = new Button(SearchMealActivity.this);
            btnAddToFavorites.setText("Añadir a favoritos");
            btnAddToFavorites.setOnClickListener(v -> addToFavorites(foodName));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 8, 0, 8);
            btnAddToFavorites.setLayoutParams(layoutParams);

            LinearLayout itemLayout = new LinearLayout(SearchMealActivity.this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.addView(textView);
            itemLayout.addView(btnAddToFavorites);

            llSearchResults.addView(itemLayout);
        }
    }

    private void addToFavorites(String foodName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(uid);


            Map<String, Object> foodData = new HashMap<>();
            foodData.put("name", foodName);

            userRef.collection("favoriteFoods")
                    .document()
                    .set(foodData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(SearchMealActivity.this, foodName + " añadido a favoritos", Toast.LENGTH_SHORT).show();
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
