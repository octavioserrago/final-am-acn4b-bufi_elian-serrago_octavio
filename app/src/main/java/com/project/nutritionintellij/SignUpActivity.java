package com.project.nutritionintellij;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputLayout textInputLayoutSignUpEmail;
    private TextInputLayout textInputLayoutSignUpPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textInputLayoutSignUpEmail = findViewById(R.id.textInputLayoutSignUpEmail);
        textInputLayoutSignUpPassword = findViewById(R.id.textInputLayoutSignUpPassword);
    }

    public void signUp(View v) {
        final String email = textInputLayoutSignUpEmail.getEditText().getText().toString().trim();
        String password = textInputLayoutSignUpPassword.getEditText().getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(SignUpActivity.this, "Sign Up successful", Toast.LENGTH_SHORT).show();


                    String userID = mAuth.getCurrentUser().getUid();


                    db.collection("users").document(userID)
                            .set(new User(email, System.currentTimeMillis()), SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(SignUpActivity.this, "User document created successfully", Toast.LENGTH_SHORT).show();
                                    } else {

                                        Toast.makeText(SignUpActivity.this, "Failed to create user document: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    // Puedes navegar a otra actividad o actualizar la UI aquí
                    finish(); // Cerrar la actividad de registro
                } else {
                    // Error al crear usuario
                    Toast.makeText(SignUpActivity.this, "Sign Up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

class User {
    private String email;
    private long created_at;

    public User(String email, long created_at) {
        this.email = email;
        this.created_at = created_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}



