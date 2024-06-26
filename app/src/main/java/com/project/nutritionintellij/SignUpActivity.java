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
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout textInputLayoutSignUpEmail;
    private TextInputLayout textInputLayoutSignUpPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        textInputLayoutSignUpEmail = findViewById(R.id.textInputLayoutSignUpEmail);
        textInputLayoutSignUpPassword = findViewById(R.id.textInputLayoutSignUpPassword);
    }

    public void signUp(View v) {
        String email = textInputLayoutSignUpEmail.getEditText().getText().toString().trim();
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
                    // Navigate to another activity or update the UI
                    finish(); // Close the sign-up activity
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign Up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}