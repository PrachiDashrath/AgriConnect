package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilContact, tilEmail, tilPassword, tilRole;
    private TextInputEditText etFullName, etContactNumber, etEmail, etPassword;
    private AutoCompleteTextView roleDropdown;
    private Button btnRegister;
    private TextView tvLoginRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        // Views
        tilName         = findViewById(R.id.tilName);
        tilContact      = findViewById(R.id.tilContact);
        tilEmail        = findViewById(R.id.tilEmail);
        tilPassword     = findViewById(R.id.tilPassword);
        tilRole         = findViewById(R.id.tilRole);
        etFullName      = findViewById(R.id.etFullName);
        etContactNumber = findViewById(R.id.etContactNumber);
        etEmail         = findViewById(R.id.etEmail);
        etPassword      = findViewById(R.id.etPassword);
        roleDropdown    = findViewById(R.id.roleDropdown);
        btnRegister     = findViewById(R.id.btnRegister);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        // Role dropdown options
        String[] roles = {"Farmer", "Buyer", "Inspector", "Transporter"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, roles);
        roleDropdown.setAdapter(adapter);

        // Buttons
        btnRegister.setOnClickListener(v -> registerUser());

        tvLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        // Get values
        String name     = etFullName.getText()      != null ? etFullName.getText().toString().trim()      : "";
        String contact  = etContactNumber.getText() != null ? etContactNumber.getText().toString().trim() : "";
        String email    = etEmail.getText()         != null ? etEmail.getText().toString().trim()         : "";
        String password = etPassword.getText()      != null ? etPassword.getText().toString().trim()      : "";
        String role     = roleDropdown.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Enter your name");
            return;
        }
        tilName.setError(null);

        if (TextUtils.isEmpty(contact) || contact.length() < 10) {
            tilContact.setError("Enter valid contact number");
            return;
        }
        tilContact.setError(null);

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Enter your email");
            return;
        }
        tilEmail.setError(null);

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            return;
        }
        tilPassword.setError(null);

        if (TextUtils.isEmpty(role)) {
            tilRole.setError("Select your role");
            return;
        }
        tilRole.setError(null);

        // Disable button to prevent double tap
        btnRegister.setEnabled(false);
        btnRegister.setText("Please wait...");

        // Step 1: Create Firebase Auth account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = authResult.getUser().getUid();

                    // Step 2: Save profile to Realtime Database
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name",      name);
                    userMap.put("contact",   contact);
                    userMap.put("email",     email);
                    userMap.put("userType",  role);   // ← This is what Login reads

                    mDatabase.child("users").child(uid)
                            .setValue(userMap)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(RegisterActivity.this,
                                        "Registration successful!", Toast.LENGTH_SHORT).show();

                                // Sign out so user logs in fresh
                                mAuth.signOut();

                                // Step 3: Go to Login
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnRegister.setEnabled(true);
                                btnRegister.setText("Register");
                                Toast.makeText(RegisterActivity.this,
                                        "Failed to save profile: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Register");
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}