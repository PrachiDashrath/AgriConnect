package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserSelectionActivity extends AppCompatActivity {

    private static final String TAG = "UserSelectionActivity";

    private LinearLayout btnFarmer, btnBuyer, btnInspector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        btnFarmer = findViewById(R.id.btnFarmer);
        btnBuyer = findViewById(R.id.btnBuyer);
        btnInspector = findViewById(R.id.btnInspector);

        btnFarmer.setOnClickListener(v -> openDashboard("farmer"));
        btnBuyer.setOnClickListener(v -> openDashboard("buyer"));
        btnInspector.setOnClickListener(v -> openDashboard("inspector"));
    }

    private void openDashboard(String role) {
        Log.d(TAG, "Role clicked: " + role);

        Intent intent;

        switch (role) {
            case "farmer":
                intent = new Intent(this, FarmerFormActivity.class);
                break;
            case "buyer":
                intent = new Intent(this, BuyerFormActivity.class);
                break;
            case "inspector":
                intent = new Intent(this, InspectorFormActivity.class);
                break;
            default:
                Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish();
    }
}
