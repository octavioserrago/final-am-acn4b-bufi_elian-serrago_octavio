package com.project.nutritionintellij;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateFoodActivity extends AppCompatActivity {

    private static final String TAG = "CreateFoodActivity";

    private EditText editTextFoodName;
    private EditText editTextImgUrl;
    private EditText editTextKcals;
    private Button buttonSaveFood;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextImgUrl = findViewById(R.id.editTextImgUrl);
        editTextKcals = findViewById(R.id.editTextKcals);
        buttonSaveFood = findViewById(R.id.buttonSaveFood);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        buttonSaveFood.setOnClickListener(v -> saveFood());
    }

    private void saveFood() {
        String foodName = editTextFoodName.getText().toString().trim();
        String imgUrl = editTextImgUrl.getText().toString().trim();
        String kcalsStr = editTextKcals.getText().toString().trim();

        if (foodName.isEmpty() || imgUrl.isEmpty() || kcalsStr.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double kcals;
        try {
            kcals = Double.parseDouble(kcalsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid calories value", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();


            db.collection("foods")
                    .add(new Food(foodName, imgUrl, kcals))
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CreateFoodActivity.this, "Food added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding food: " + e.getMessage());
                        Toast.makeText(CreateFoodActivity.this, "Error adding food", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
