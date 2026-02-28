package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agriconnect.activities.TransporterRegisterActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilLoginEmail, tilLoginPassword;
    private TextInputEditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private ProgressBar loginProgressBar;
    private TextView tvRegisterRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        // Views
        tilLoginEmail    = findViewById(R.id.tilLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        etLoginEmail     = findViewById(R.id.etLoginEmail);
        etLoginPassword  = findViewById(R.id.etLoginPassword);
        btnLogin         = findViewById(R.id.btnLogin);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        tvRegisterRedirect = findViewById(R.id.tvRegisterRedirect);

        // Buttons
        btnLogin.setOnClickListener(v -> loginUser());

        tvRegisterRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email    = etLoginEmail.getText()    != null ? etLoginEmail.getText().toString().trim()    : "";
        String password = etLoginPassword.getText() != null ? etLoginPassword.getText().toString().trim() : "";

        // Validate
        if (TextUtils.isEmpty(email)) {
            tilLoginEmail.setError("Enter your email");
            return;
        }
        tilLoginEmail.setError(null);

        if (TextUtils.isEmpty(password)) {
            tilLoginPassword.setError("Enter your password");
            return;
        }
        tilLoginPassword.setError(null);

        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Please wait...");
        loginProgressBar.setVisibility(View.VISIBLE);

        // Step 1: Authenticate with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = authResult.getUser().getUid();

                    // Step 2: Read userType from database
                    mDatabase.child("users").child(uid).child("userType")
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    loginProgressBar.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                    btnLogin.setText("Login");

                                    if (!snapshot.exists()) {
                                        Toast.makeText(LoginActivity.this,
                                                "Profile not found. Please register again.",
                                                Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        return;
                                    }

                                    String userType = snapshot.getValue(String.class);

                                    // Step 3: Route to correct dashboard
                                    goToDashboard(userType);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    loginProgressBar.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                    btnLogin.setText("Login");
                                    Toast.makeText(LoginActivity.this,
                                            "Error: " + error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    loginProgressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void goToDashboard(String userType) {
        Intent intent;

        switch (userType) {
            case "Farmer":
                intent = new Intent(LoginActivity.this, FarmerDashboardActivity.class);
                break;
            case "Buyer":
                intent = new Intent(LoginActivity.this, BuyerDashboardActivity.class);
                break;
            case "Inspector":
                intent = new Intent(LoginActivity.this, InspectorDashboardActivity.class);
                break;
            case "Transporter":
                intent = new Intent(LoginActivity.this, TransporterRegisterActivity.class);
                break;
            default:
                Toast.makeText(this, "Unknown role: " + userType, Toast.LENGTH_SHORT).show();
                return;
        }

        // Clear back stack — user cannot press Back to return to login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}