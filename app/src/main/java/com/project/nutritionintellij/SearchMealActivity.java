package com.project.nutritionintellij;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
            textView.setOnClickListener(v -> addToFavorites(foodName));
            llSearchResults.addView(textView);
        }
    }

    private void addToFavorites(String foodName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.collection("favoriteFoods")
                    .whereEqualTo("name", foodName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Si no existe, añadir el alimento
                                userRef.collection("favoriteFoods").add(new Food(foodName))
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(SearchMealActivity.this, foodName + " añadido a favoritos", Toast.LENGTH_SHORT).show();
                                            navigateToDashboard();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error al añadir alimento favorito: " + e.getMessage());
                                            Toast.makeText(SearchMealActivity.this, "Error al añadir alimento favorito", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(SearchMealActivity.this, foodName + " ya está en favoritos", Toast.LENGTH_SHORT).show();
                                navigateToDashboard();
                            }
                        } else {
                            Log.e(TAG, "Error comprobando existencia de alimento favorito: " + task.getException());
                            Toast.makeText(SearchMealActivity.this, "Error comprobando existencia de alimento favorito", Toast.LENGTH_SHORT).show();
                        }
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