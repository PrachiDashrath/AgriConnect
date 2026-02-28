package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class CheckoutActivity extends AppCompatActivity {

    private TextInputEditText etQty, etLocation;
    private String cropName, pricePerKg, farmerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Get data from previous Intent
        cropName = getIntent().getStringExtra("cropName");
        pricePerKg = getIntent().getStringExtra("price");
        farmerPhone = getIntent().getStringExtra("farmerPhone");

        TextView tvName = findViewById(R.id.tvCheckoutCropName);
        tvName.setText("Ordering: " + cropName);

        etQty = findViewById(R.id.etCheckoutQty);
        etLocation = findViewById(R.id.etCheckoutLocation);
        Button btnBill = findViewById(R.id.btnGenerateBill);

        btnBill.setOnClickListener(v -> {
            String qty = etQty.getText().toString();
            String loc = etLocation.getText().toString();

            if (qty.isEmpty() || loc.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Math: Total = Price * Qty
            double total = Double.parseDouble(pricePerKg) * Double.parseDouble(qty);

            Order order = new Order(cropName, loc, qty, String.valueOf(total), farmerPhone);

            Intent intent = new Intent(this, BillActivity.class);
            intent.putExtra("order_data", order);
            startActivity(intent);
        });
    }
}