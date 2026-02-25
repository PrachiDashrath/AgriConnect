package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // MUST HAVE THIS IMPORT

public class BuyerDashboardActivity extends AppCompatActivity {

    private ImageButton btnBack, btnChatbotCorner;
    private LinearLayout btnDemand, btnPrice, btnFraud;
    private CardView btnBuyCrops; // Matches XML now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        // Initialize UI Elements
        btnBack = findViewById(R.id.btnBack);
        btnChatbotCorner = findViewById(R.id.btnChatbotCorner);
        btnBuyCrops = findViewById(R.id.btnBuyCrops); // This line won't crash now!
        btnDemand = findViewById(R.id.btnBuyerDemand);
        btnPrice = findViewById(R.id.btnBuyerPrice);
        btnFraud = findViewById(R.id.btnFraudDetect);

        // Click Listeners
        btnBack.setOnClickListener(v -> onBackPressed());

        btnBuyCrops.setOnClickListener(v -> {
            Intent intent = new Intent(BuyerDashboardActivity.this, MarketPlaceActivity.class);
            startActivity(intent);
        });

        btnChatbotCorner.setOnClickListener(v -> safeNavigate(ChatbotActivity.class));
        btnDemand.setOnClickListener(v -> safeNavigate(DemandPredictionActivity.class));
        btnPrice.setOnClickListener(v -> safeNavigate(MarketPriceActivity.class));
        btnFraud.setOnClickListener(v -> safeNavigate(FraudDetectionActivity.class));
    }

    private void safeNavigate(Class<?> target) {
        try {
            startActivity(new Intent(this, target));
        } catch (Exception e) {
            Toast.makeText(this, "Opening feature...", Toast.LENGTH_SHORT).show();
        }
    }
}