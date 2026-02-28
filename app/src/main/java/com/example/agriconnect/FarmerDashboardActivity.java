package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // ✅ Added this import

public class FarmerDashboardActivity extends AppCompatActivity {

    private ImageButton btnBack, btnChatbotCorner;
    // ✅ FIXED: Changed from LinearLayout to CardView to prevent ClassCastException
    private CardView cropCard, addCropCard, demandCard, weatherCard, marketCard, govtCard, voiceAssistantCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_dashboard);

        // Initialize UI Elements
        btnBack = findViewById(R.id.btnBack);
        btnChatbotCorner = findViewById(R.id.btnChatbotCorner);

        // ✅ FIXED: These now correctly match the CardView types in your XML
        cropCard = findViewById(R.id.crop_card);
        addCropCard = findViewById(R.id.add_crop_card);
        demandCard = findViewById(R.id.demand_card);
        weatherCard = findViewById(R.id.weather_card);
        marketCard = findViewById(R.id.market_card);
        govtCard = findViewById(R.id.govt_card);
        voiceAssistantCard = findViewById(R.id.voice_assistant_card);

        // Back Button Logic
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(FarmerDashboardActivity.this, UserSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Dashboard Navigation
        if (addCropCard != null) addCropCard.setOnClickListener(v -> startActivity(new Intent(this, AddCropActivity.class)));
        if (cropCard != null) cropCard.setOnClickListener(v -> startActivity(new Intent(this, CropManagementActivity.class)));
        if (demandCard != null) demandCard.setOnClickListener(v -> startActivity(new Intent(this, DemandPredictionActivity.class)));
        if (weatherCard != null) weatherCard.setOnClickListener(v -> startActivity(new Intent(this, WeatherActivity.class)));
        if (marketCard != null) marketCard.setOnClickListener(v -> startActivity(new Intent(this, MarketPriceActivity.class)));
        if (govtCard != null) govtCard.setOnClickListener(v -> startActivity(new Intent(this, GovtSchemeActivity.class)));

        // Voice Assistant Card
        if (voiceAssistantCard != null) {
            voiceAssistantCard.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(FarmerDashboardActivity.this, VoiceDemandActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Opening Voice Assistant...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Chatbot Button
        if (btnChatbotCorner != null) {
            btnChatbotCorner.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(this, ChatbotActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Chatbot coming soon!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}