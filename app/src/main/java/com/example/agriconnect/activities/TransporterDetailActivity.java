package com.example.agriconnect.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.agriconnect.R;
import com.google.firebase.auth.FirebaseAuth;

public class TransporterDetailActivity extends AppCompatActivity {

    private TextView tvCompanyName, tvOwnerName, tvContact, tvLocation;
    private TextView tvVehicleType, tvPricePerKm, tvGstNo, tvLicenseNo;
    private TextView tvTotalTrips, tvVerified;
    // REMOVED: tvRating and ratingBar (not in XML)

    private Button btnBookNow, btnCall, btnShare;
    private CardView cardBusiness;

    private String transporterId, transporterName, transporterVehicle;
    private String transporterContact, transporterLocation, transporterPrice;
    private boolean transporterVerified;
    private double transporterRating;
    private String transporterGst, transporterLicense;
    private int transporterTotalTrips;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter_detail);

        // Get data from intent
        transporterId       = getIntent().getStringExtra("transporterId");
        transporterName     = getIntent().getStringExtra("transporterName");
        transporterVehicle  = getIntent().getStringExtra("transporterVehicle");
        transporterContact  = getIntent().getStringExtra("transporterContact");
        transporterLocation = getIntent().getStringExtra("transporterLocation");
        transporterPrice    = getIntent().getStringExtra("transporterPrice");
        transporterVerified = getIntent().getBooleanExtra("transporterVerified", false);
        transporterRating   = getIntent().getDoubleExtra("transporterRating", 0.0);
        transporterGst      = getIntent().getStringExtra("transporterGst");
        transporterLicense  = getIntent().getStringExtra("transporterLicense");
        transporterTotalTrips = getIntent().getIntExtra("transporterTotalTrips", 0);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        displayTransporterDetails();
        setupClickListeners();
    }

    private void initViews() {
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvOwnerName   = findViewById(R.id.tvOwnerName);
        tvContact     = findViewById(R.id.tvContact);
        tvLocation    = findViewById(R.id.tvLocation);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        tvPricePerKm  = findViewById(R.id.tvPricePerKm);
        tvGstNo       = findViewById(R.id.tvGstNo);
        tvLicenseNo   = findViewById(R.id.tvLicenseNo);
        tvTotalTrips  = findViewById(R.id.tvTotalTrips);
        tvVerified    = findViewById(R.id.tvVerified);
        btnBookNow    = findViewById(R.id.btnBookNow);
        btnCall       = findViewById(R.id.btnCall);
        btnShare      = findViewById(R.id.btnShare);
        cardBusiness  = findViewById(R.id.cardBusiness);
    }

    private void displayTransporterDetails() {
        tvCompanyName.setText(transporterName != null ? transporterName : "Company Name");
        tvOwnerName.setText("Owner: " + (transporterName != null ? transporterName : "Not specified"));
        tvContact.setText("📞 " + (transporterContact != null ? transporterContact : "N/A"));
        tvLocation.setText("📍 " + (transporterLocation != null ? transporterLocation : "N/A"));
        tvVehicleType.setText("🚚 " + (transporterVehicle != null ? transporterVehicle : "N/A"));
        tvPricePerKm.setText("💰 ₹" + (transporterPrice != null ? transporterPrice : "0") + " per km");
        tvGstNo.setText("GST: " + (transporterGst != null ? transporterGst : "Not available"));
        tvLicenseNo.setText("License: " + (transporterLicense != null ? transporterLicense : "Not available"));
        tvTotalTrips.setText("Total Trips: " + transporterTotalTrips);
        // Rating shown as text since RatingBar was removed
        // tvRating and ratingBar removed — add to XML if you want them

        if (transporterVerified) {
            tvVerified.setText("✓ Verified Transporter");
            tvVerified.setTextColor(0xFF388E3C); // green
        } else {
            tvVerified.setText("⚠ Not Verified");
            tvVerified.setTextColor(0xFFF57C00); // orange
        }
        tvVerified.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        // Single book button - no duplicate
        btnBookNow.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Please login to book", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, com.example.agriconnect.LoginActivity.class));
                return;
            }
            Intent intent = new Intent(this, TransportBookingFormActivity.class);
            intent.putExtra("transporterId", transporterId);
            intent.putExtra("transporterName", transporterName);
            intent.putExtra("transporterContact", transporterContact);
            intent.putExtra("transporterGst", transporterGst);
            intent.putExtra("transporterPrice", transporterPrice);
            intent.putExtra("transporterVehicle", transporterVehicle);
            startActivity(intent);
        });

        btnCall.setOnClickListener(v -> {
            if (transporterContact != null && !transporterContact.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + transporterContact));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Contact number not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(v -> {
            String shareText = "Company: " + transporterName + "\n"
                    + "Contact: " + transporterContact + "\n"
                    + "Location: " + transporterLocation + "\n"
                    + "Vehicle: " + transporterVehicle + "\n"
                    + "Price: ₹" + transporterPrice + "/km\n"
                    + "Rating: " + String.format("%.1f", transporterRating) + " ⭐";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Transporter Details");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }
}