package com.example.agriconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agriconnect.models.Transporter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FinalBillActivity extends AppCompatActivity {

    private String grandTotalAmount = "0";
    private Order currentOrder;
    private Transporter currentTransporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_bill);

        // FIX: Assigning to class variables so they are accessible globally in this class
        currentOrder = (Order) getIntent().getSerializableExtra("order_data");
        currentTransporter = (Transporter) getIntent().getSerializableExtra("transporter_data");

        TextView tvCrop = findViewById(R.id.tvFinalCropCost);
        TextView tvTrans = findViewById(R.id.tvFinalTransportCost);
        TextView tvGrand = findViewById(R.id.tvGrandTotal);
        Button btnPay = findViewById(R.id.btnFinalPay);

        if (currentOrder != null && currentTransporter != null) {
            double cropCost = Double.parseDouble(currentOrder.totalBill);

            // Calculation logic
            double distance = 50.0; // You can later make this dynamic
            double transportPricePerKm = Double.parseDouble(currentTransporter.pricePerKm);
            double transportCost = transportPricePerKm * distance;
            double grandTotal = cropCost + transportCost;

            grandTotalAmount = String.valueOf(grandTotal);

            tvCrop.setText("Crop: ₹" + cropCost);
            tvTrans.setText("Transport (" + currentTransporter.getName() + "): ₹" + transportCost);
            tvGrand.setText("Total: ₹" + grandTotalAmount);
        }

        btnPay.setOnClickListener(v -> payWithUPI(grandTotalAmount));
    }

    private void payWithUPI(String amount) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "yourname@upi") // TODO: Use real UPI ID
                .appendQueryParameter("pn", "AgriConnect")
                .appendQueryParameter("tn", "Payment for " + currentOrder.cropName)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with...");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, 123);
        } else {
            Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            // Note: Most UPI apps return RESULT_OK even if cancelled.
            // Real verification happens on your server/bank side.
            saveBookingToFirebase();
        }
    }

    private void saveBookingToFirebase() {
        if (currentOrder == null || currentTransporter == null) return;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("bookings");
        String id = db.push().getKey();

        // FIX: Using dynamic data instead of hardcoded strings
        Booking booking = new Booking(
                id,
                currentOrder.cropName,
                grandTotalAmount,
                currentOrder.buyerLocation,
                "Paid",
                currentTransporter.getName(),
                currentOrder.farmerPhone
        );

        if (id != null) {
            db.child(id).setValue(booking).addOnSuccessListener(aVoid -> {
                Intent intent = new Intent(this, PaymentSuccessActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Booking Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}