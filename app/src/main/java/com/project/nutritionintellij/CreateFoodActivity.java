package com.project.nutritionintellij;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class CreateFoodActivity extends AppCompatActivity {

    private EditText editTextFoodName;
    private EditText editTextImgUrl;
    private EditText editTextKcals;
    private Button buttonSaveFood;
    private Button buttonBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextImgUrl = findViewById(R.id.editTextImgUrl);
        editTextKcals = findViewById(R.id.editTextKcals);
        buttonSaveFood = findViewById(R.id.buttonSaveFood);
        buttonBack = findViewById(R.id.buttonBack);

        db = FirebaseFirestore.getInstance();

        buttonSaveFood.setOnClickListener(v -> saveFood());
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void saveFood() {
        String foodName = editTextFoodName.getText().toString().trim();
        String imgUrl = editTextImgUrl.getText().toString().trim();
        String kcalsStr = editTextKcals.getText().toString().trim();

        if (foodName.isEmpty() || imgUrl.isEmpty() || kcalsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double kcals = Double.parseDouble(kcalsStr);

            Food newFood = new Food(foodName, imgUrl, kcals);
            db.collection("foods").document(foodName).set(newFood)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CreateFoodActivity.this, "Food saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateFoodActivity.this, "Error saving food", Toast.LENGTH_SHORT).show();
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid calories format", Toast.LENGTH_SHORT).show();
        }
    }
}
