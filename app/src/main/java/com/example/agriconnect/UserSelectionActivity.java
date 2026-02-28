package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agriconnect.activities.TransporterRegisterActivity;

public class UserSelectionActivity extends AppCompatActivity {

    private static final String TAG = "UserSelectionActivity";
    // Added btnTransport
    private LinearLayout btnFarmer, btnBuyer, btnInspector, btnTransport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        btnFarmer = findViewById(R.id.btnFarmer);
        btnBuyer = findViewById(R.id.btnBuyer);
        btnInspector = findViewById(R.id.btnInspector);
        btnTransport = findViewById(R.id.btnTransport); // Initialize new button

        btnFarmer.setOnClickListener(v -> openDashboard("farmer"));
        btnBuyer.setOnClickListener(v -> openDashboard("buyer"));
        btnInspector.setOnClickListener(v -> openDashboard("inspector"));
        btnTransport.setOnClickListener(v -> openDashboard("transport")); // New Listener
    }

    private void openDashboard(String role) {
        Log.d(TAG, "Role clicked: " + role);

        Intent intent;

        switch (role) {
            case "farmer":
                intent = new Intent(this, FarmerDashboardActivity.class);
                break;
            case "buyer":
                intent = new Intent(this, BuyerDashboardActivity.class);
                break;
            case "inspector":
                intent = new Intent(this, InspectorFormActivity.class);
                break;
            case "transport":
                // Opens the registration form we created earlier
                intent = new Intent(this, TransporterRegisterActivity.class);
                break;
            default:
                Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish();
    }
}