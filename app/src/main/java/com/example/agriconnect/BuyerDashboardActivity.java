package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.agriconnect.activities.TransportTrackingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BuyerDashboardActivity extends AppCompatActivity {

    private ImageButton btnBack, btnChatbotCorner;
    private LinearLayout btnDemand, btnPrice, btnFraud;
    private CardView btnBuyCrops, btnBuyBulk;
    private Button btnTrackPackage;

    private Button btnBookTransport;

    // Firebase Auth
    private FirebaseAuth mAuth;

    // This should ideally be fetched from SharedPreferences after user logs in
    private String userCity = "Pune";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        btnBack = findViewById(R.id.btnBack);
        btnChatbotCorner = findViewById(R.id.btnChatbotCorner);
        btnBuyCrops = findViewById(R.id.btnBuyCrops);
        btnBuyBulk = findViewById(R.id.btnBuyBulk);
        btnDemand = findViewById(R.id.btnBuyerDemand);
        btnPrice = findViewById(R.id.btnBuyerPrice);
        btnFraud = findViewById(R.id.btnFraudDetect);

        // Initialize Track Package Button
        btnTrackPackage = findViewById(R.id.btnTrackPackage);

        btnBookTransport = findViewById(R.id.btnBookTransport);
        if (btnBookTransport != null) {
            btnBookTransport.setOnClickListener(v -> {
                startActivity(new Intent(BuyerDashboardActivity.this,
                        com.example.agriconnect.activities.TransportCompanyListActivity.class));
            });
        }

        // Track Package Button Click Listener
        if (btnTrackPackage != null) {
            btnTrackPackage.setOnClickListener(v -> {
                // Check if user is logged in
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // Show list of active trips for this buyer
                    Intent intent = new Intent(BuyerDashboardActivity.this, TransportTrackingActivity.class);
                    intent.putExtra("userId", currentUser.getUid());
                    intent.putExtra("userType", "buyer");
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please login to track packages", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
            });
        }

        // Back Button - Redirects to UserSelectionActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(BuyerDashboardActivity.this, UserSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Standard Purchase Intent
        btnBuyCrops.setOnClickListener(v -> {
            Intent intent = new Intent(BuyerDashboardActivity.this, BuyerFormActivity.class);
            intent.putExtra("isBulkMode", false);
            intent.putExtra("buyerLocation", userCity);
            startActivity(intent);
        });

        // Bulk Purchase Intent
        btnBuyBulk.setOnClickListener(v -> {
            Intent bulkIntent = new Intent(BuyerDashboardActivity.this, BuyerFormActivity.class);
            bulkIntent.putExtra("isBulkMode", true);
            bulkIntent.putExtra("buyerLocation", userCity);
            startActivity(bulkIntent);
            Toast.makeText(this, "Bulk Mode: Minimum 500kg for discount.", Toast.LENGTH_LONG).show();
        });

        // Navigation for other features
        btnChatbotCorner.setOnClickListener(v -> safeNavigate(ChatbotActivity.class));
        btnDemand.setOnClickListener(v -> safeNavigate(DemandPredictionActivity.class));
        btnPrice.setOnClickListener(v -> safeNavigate(MarketPlaceActivity.class));
        btnFraud.setOnClickListener(v -> safeNavigate(FraudDetectionActivity.class));
    }

    private void safeNavigate(Class<?> target) {
        try {
            startActivity(new Intent(this, target));
        } catch (Exception e) {
            Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
        }
    }
}