package com.example.agriconnect;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FarmerNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_notification);

        recyclerView = findViewById(R.id.recyclerNotifications);
        tvEmpty = findViewById(R.id.tvEmpty);

        // TEMP farmer name (later Firebase UID)
        String farmerName = "Raj";

        List<FarmerNotification> notifications =
                DummyDatabase.getNotificationsForFarmer(farmerName);

        if (notifications.isEmpty()) {
            tvEmpty.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            tvEmpty.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new com.example.agriconnect.FarmerNotificationAdapter(notifications));
        }
    }
}
