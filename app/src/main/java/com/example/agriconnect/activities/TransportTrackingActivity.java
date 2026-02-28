package com.example.agriconnect.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.agriconnect.R;
import com.example.agriconnect.models.TransportBooking;

public class TransportTrackingActivity extends AppCompatActivity {

    private TextView tvBookingId, tvTransporterName, tvCustomerName;
    private TextView tvPickup, tvDestination, tvStatus, tvDate, tvDistance;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    private String bookingId;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_tracking);

        bookingId = getIntent().getStringExtra("bookingId");

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );
        bookingsRef = database.getReference("transport_bookings");

        initViews();

        // Pre-fill from intent extras while Firebase loads
        String transporterName = getIntent().getStringExtra("transporterName");
        String customerName = getIntent().getStringExtra("customerName");
        String pickup = getIntent().getStringExtra("pickup");
        String destination = getIntent().getStringExtra("destination");

        if (transporterName != null) tvTransporterName.setText("Transporter: " + transporterName);
        if (customerName != null) tvCustomerName.setText("Customer: " + customerName);
        if (pickup != null) tvPickup.setText("From: " + pickup);
        if (destination != null) tvDestination.setText("To: " + destination);

        if (bookingId != null) {
            loadBookingDetails();
        } else {
            progressBar.setVisibility(View.GONE);
            tvStatus.setText("Booking ID not found");
            Toast.makeText(this, "No booking ID provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        tvBookingId = findViewById(R.id.tvBookingId);
        tvTransporterName = findViewById(R.id.tvTransporterName);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvPickup = findViewById(R.id.tvPickup);
        tvDestination = findViewById(R.id.tvDestination);
        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);
        tvDistance = findViewById(R.id.tvDistance);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBookingDetails() {
        progressBar.setVisibility(View.VISIBLE);

        bookingsRef.child(bookingId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                TransportBooking booking = snapshot.getValue(TransportBooking.class);
                if (booking != null) {
                    displayBooking(booking);
                } else {
                    tvStatus.setText("Booking not found");
                    Toast.makeText(TransportTrackingActivity.this,
                            "Booking data not found in Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransportTrackingActivity.this,
                        "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBooking(TransportBooking booking) {
        tvBookingId.setText("Booking ID: " + (booking.getBookingId() != null ? booking.getBookingId() : bookingId));
        tvTransporterName.setText("Transporter: " + (booking.getTransporterName() != null ? booking.getTransporterName() : "N/A"));
        tvCustomerName.setText("Customer: " + (booking.getCustomerName() != null ? booking.getCustomerName() : "N/A"));
        tvPickup.setText("From: " + (booking.getPickupAddress() != null ? booking.getPickupAddress() : "N/A"));
        tvDestination.setText("To: " + (booking.getDestinationAddress() != null ? booking.getDestinationAddress() : "N/A"));
        tvDate.setText("Date: " + (booking.getBookingDate() != null ? booking.getBookingDate() : "N/A"));
        tvDistance.setText(String.format("Distance: %.2f km", booking.getDistanceKm()));

        // Status with color
        String status = booking.getStatus() != null ? booking.getStatus() : "Confirmed";
        tvStatus.setText("Status: " + status);
        switch (status) {
            case "Confirmed":
                tvStatus.setTextColor(0xFF1565C0); // blue
                break;
            case "In Transit":
                tvStatus.setTextColor(0xFFFF9800); // orange
                break;
            case "Delivered":
                tvStatus.setTextColor(0xFF2E7D32); // green
                break;
            default:
                tvStatus.setTextColor(0xFF757575); // grey
        }
    }
}