package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FarmerDashboardActivity extends AppCompatActivity {

    private LinearLayout cropCard, weatherCard, marketCard, govtCard, addCropCard, demandCard;
    private ImageButton btnBack, btnChatbotCorner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_dashboard);

        // Initialize UI
        btnBack          = findViewById(R.id.btnBack);
        btnChatbotCorner = findViewById(R.id.btnChatbotCorner);
        cropCard         = findViewById(R.id.crop_card);
        weatherCard      = findViewById(R.id.weather_card);
        marketCard       = findViewById(R.id.market_card);
        govtCard         = findViewById(R.id.govt_card);
        addCropCard      = findViewById(R.id.add_crop_card);
        demandCard       = findViewById(R.id.demand_card);

        // Click Listeners
        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());

        if (btnChatbotCorner != null) {
            btnChatbotCorner.setOnClickListener(v -> safeNavigate(ChatbotActivity.class));
        }

        if (cropCard != null) cropCard.setOnClickListener(v -> safeNavigate(CropManagementActivity.class));
        if (addCropCard != null) addCropCard.setOnClickListener(v -> safeNavigate(AddCropActivity.class));
        if (weatherCard != null) weatherCard.setOnClickListener(v -> safeNavigate(WeatherActivity.class));
        if (marketCard != null) marketCard.setOnClickListener(v -> safeNavigate(MarketPriceActivity.class));
        if (govtCard != null) govtCard.setOnClickListener(v -> safeNavigate(GovtSchemeActivity.class));
        if (demandCard != null) demandCard.setOnClickListener(v -> safeNavigate(DemandPredictionActivity.class));
    }

    private void safeNavigate(Class<?> target) {
        try {
            startActivity(new Intent(this, target));
        } catch (Exception e) {
            Toast.makeText(this, "Opening feature...", Toast.LENGTH_SHORT).show();
        }
    }
}