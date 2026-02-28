package com.example.agriconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.agriconnect.R;
import com.example.agriconnect.models.TransportBill;
import com.example.agriconnect.models.TransportBooking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransportBookingFormActivity extends AppCompatActivity {

    private EditText etCustomerName, etCustomerPhone, etPickup, etDestination, etDistance, etWeight;
    private TextView tvTransporterName, tvPricePerKm, tvEstimatedCost;
    private Button btnCalculate, btnConfirmBooking;
    private ProgressBar progressBar;

    private String transporterId, transporterName, transporterContact;
    private String transporterGst, pricePerKmStr;
    private double pricePerKm = 0;

    private DatabaseReference bookingsRef, billsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_booking_form);

        // Get transporter data from intent
        transporterId = getIntent().getStringExtra("transporterId");
        transporterName = getIntent().getStringExtra("transporterName");
        transporterContact = getIntent().getStringExtra("transporterContact");
        transporterGst = getIntent().getStringExtra("transporterGst");
        pricePerKmStr = getIntent().getStringExtra("transporterPrice");

        try {
            pricePerKm = Double.parseDouble(pricePerKmStr != null ? pricePerKmStr : "0");
        } catch (NumberFormatException e) {
            pricePerKm = 10; // default fallback
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );
        bookingsRef = database.getReference("transport_bookings");
        billsRef = database.getReference("transport_bills");

        initViews();
    }

    private void initViews() {
        tvTransporterName = findViewById(R.id.tvTransporterName);
        tvPricePerKm = findViewById(R.id.tvPricePerKm);
        tvEstimatedCost = findViewById(R.id.tvEstimatedCost);
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etPickup = findViewById(R.id.etPickup);
        etDestination = findViewById(R.id.etDestination);
        etDistance = findViewById(R.id.etDistance);
        etWeight = findViewById(R.id.etWeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        progressBar = findViewById(R.id.progressBar);

        tvTransporterName.setText("Transporter: " + (transporterName != null ? transporterName : "N/A"));
        tvPricePerKm.setText("Rate: ₹" + pricePerKmStr + "/km");

        btnCalculate.setOnClickListener(v -> calculateCost());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private double getDistance() {
        try {
            return Double.parseDouble(etDistance.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void calculateCost() {
        double distance = getDistance();
        if (distance <= 0) {
            etDistance.setError("Enter valid distance");
            return;
        }

        double baseFare = distance * pricePerKm;
        double toll = distance > 50 ? 200 : 0;
        double loading = 150;
        double unloading = 150;
        double insurance = baseFare * 0.02;
        double subtotal = baseFare + toll + loading + unloading + insurance;
        double gst = subtotal * 0.18;
        double total = subtotal + gst;

        tvEstimatedCost.setVisibility(View.VISIBLE);
        tvEstimatedCost.setText(String.format(
                "Base Fare: ₹%.0f\nToll: ₹%.0f\nLoading: ₹%.0f\nUnloading: ₹%.0f\n" +
                        "Insurance: ₹%.0f\nGST (18%%): ₹%.0f\n\nTOTAL: ₹%.0f",
                baseFare, toll, loading, unloading, insurance, gst, total));
    }

    private void confirmBooking() {
        String customerName = etCustomerName.getText().toString().trim();
        String customerPhone = etCustomerPhone.getText().toString().trim();
        String pickup = etPickup.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        double distance = getDistance();

        if (customerName.isEmpty()) { etCustomerName.setError("Required"); return; }
        if (customerPhone.isEmpty()) { etCustomerPhone.setError("Required"); return; }
        if (pickup.isEmpty()) { etPickup.setError("Required"); return; }
        if (destination.isEmpty()) { etDestination.setError("Required"); return; }
        if (distance <= 0) { etDistance.setError("Enter valid distance"); return; }

        progressBar.setVisibility(View.VISIBLE);
        btnConfirmBooking.setEnabled(false);

        // Calculate all charges
        double baseFare = distance * pricePerKm;
        double toll = distance > 50 ? 200 : 0;
        double loading = 150;
        double unloading = 150;
        double insurance = baseFare * 0.02;
        double subtotal = baseFare + toll + loading + unloading + insurance;
        double gst = subtotal * 0.18;
        double total = subtotal + gst;

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String bookingId = bookingsRef.push().getKey();
        String billId = billsRef.push().getKey();

        // Create Booking
        TransportBooking booking = new TransportBooking();
        booking.setBookingId(bookingId);
        booking.setTransporterId(transporterId);
        booking.setTransporterName(transporterName);
        booking.setCustomerName(customerName);
        booking.setCustomerPhone(customerPhone);
        booking.setPickupAddress(pickup);
        booking.setDestinationAddress(destination);
        booking.setDistanceKm(distance);
        booking.setStatus("Confirmed");
        booking.setBookingDate(date);
        booking.setBillId(billId);

        // Create Bill
        TransportBill bill = new TransportBill();
        bill.setBillId("BILL-" + System.currentTimeMillis());
        bill.setBookingId(bookingId);
        bill.setTransporterId(transporterId);
        bill.setTransporterName(transporterName);
        bill.setTransporterGst(transporterGst != null ? transporterGst : "N/A");
        bill.setCustomerName(customerName);
        bill.setCustomerPhone(customerPhone);
        bill.setPickupAddress(pickup);
        bill.setDestinationAddress(destination);
        bill.setDistanceKm(distance);
        bill.setBaseFare(baseFare);
        bill.setTollCharges(toll);
        bill.setLoadingCharges(loading);
        bill.setUnloadingCharges(unloading);
        bill.setInsuranceCharges(insurance);
        bill.setGstAmount(gst);
        bill.setTotalAmount(total);
        bill.setBillDate(date);

        // Save booking to Firebase
        bookingsRef.child(bookingId).setValue(booking)
                .addOnSuccessListener(aVoid -> {
                    // Save bill to Firebase
                    billsRef.child(billId).setValue(bill)
                            .addOnSuccessListener(aVoid2 -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show();

                                // Open BillActivity
                                Intent intent = new Intent(this, TransportBillActivity.class);
                                intent.putExtra("billId", billId);
                                intent.putExtra("bookingId", bookingId);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                btnConfirmBooking.setEnabled(true);
                                Toast.makeText(this, "Error saving bill: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnConfirmBooking.setEnabled(true);
                    Toast.makeText(this, "Error saving booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}