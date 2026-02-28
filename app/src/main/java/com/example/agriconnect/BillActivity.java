package com.example.agriconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agriconnect.activities.TransportCompanyListActivity;

public class BillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        Order order = (Order) getIntent().getSerializableExtra("order_data");

        TextView tvTotal = findViewById(R.id.tvBillTotal);
        TextView tvDetails = findViewById(R.id.tvBillDetails);
        Button btnPay = findViewById(R.id.btnPayNow);
        Button btnTransport = findViewById(R.id.btnAddTransport);

        if (order != null) {
            tvTotal.setText("₹ " + order.totalBill);
            tvDetails.setText("Item: " + order.cropName + "\n" +
                    "Quantity: " + order.quantity + " kg\n" +
                    "Delivery to: " + order.buyerLocation);
        }

        // PAY NOW
        btnPay.setOnClickListener(v -> {
            if (order == null) {
                Toast.makeText(this, "Order data missing", Toast.LENGTH_SHORT).show();
                return;
            }
            String upiUrl = "upi://pay?pa=merchant@upi&pn=AgriConnect&am=" + order.totalBill + "&cu=INR";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(upiUrl));
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show();
            }
        });

        // ADD TRANSPORT - fixed to use TransportCompanyListActivity
        btnTransport.setOnClickListener(v -> {
            Intent intent = new Intent(BillActivity.this, TransportCompanyListActivity.class);
            if (order != null) {
                intent.putExtra("targetLocation", order.buyerLocation);
            }
            startActivity(intent);
        });
    }
}