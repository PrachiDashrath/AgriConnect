package com.example.agriconnect;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CropDetailActivity extends AppCompatActivity {

    TextView tvName, tvCategory, tvPrice, tvQuantity, tvFarmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        tvName = findViewById(R.id.tvCropName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvFarmer = findViewById(R.id.tvFarmer);

        // Getting data from intent
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        double price = getIntent().getDoubleExtra("price", 0);
        int quantity = getIntent().getIntExtra("quantity", 0);
        String farmerId = getIntent().getStringExtra("farmerId");

        tvName.setText(name);
        tvCategory.setText("Category: " + category);
        tvPrice.setText("Price: ₹" + price);
        tvQuantity.setText("Available: " + quantity + " kg");
        tvFarmer.setText("Farmer ID: " + farmerId);
    }
}
