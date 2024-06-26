package com.project.nutritionintellij;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchMealActivity extends AppCompatActivity {

    private static final String TAG = "SearchMealActivity";

    private EditText etSearchQuery;
    private Button btnSearch;
    private LinearLayout llSearchResults;

    private FirebaseFirestore db;
    private List<String> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_meal);

        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnSearch = findViewById(R.id.btnSearch);
        llSearchResults = findViewById(R.id.llSearchResults);

        db = FirebaseFirestore.getInstance();

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
            llSearchResults.addView(textView);
        }
    }
}
