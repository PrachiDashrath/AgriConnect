package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class InspectorDashboardActivity extends AppCompatActivity {

    private ImageButton btnBack, btnChatbotCorner;
    private TextView tvLocation, tvPending, tvApproved, tvRejected;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private List<Crop> requestList; // Changed to MarketPrice to match your model
    private InspectorAdapter adapter;

    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector_dashboard);

        btnBack = findViewById(R.id.btnBack);
        btnChatbotCorner = findViewById(R.id.btnChatbotCorner);
        tvLocation = findViewById(R.id.tvInspectorLocation);
        tvPending = findViewById(R.id.tvPendingCount);
        tvApproved = findViewById(R.id.tvApprovedCount);
        tvRejected = findViewById(R.id.tvRejectedCount);
        recyclerView = findViewById(R.id.recyclerInspectionRequests);
        emptyState = findViewById(R.id.emptyStateLayout);

        // INITIALIZE ADAPTER IMMEDIATELY (Fixes the "No adapter attached" crash)
        requestList = new ArrayList<>();
        adapter = new InspectorAdapter(this, requestList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadInspectorProfileAndData();
    }

    private void loadInspectorProfileAndData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String city = snapshot.child("location").getValue(String.class);
                    tvLocation.setText("📍 Location: " + (city != null ? city : "Not Set"));
                    if (city != null) fetchPendingCrops(city);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchPendingCrops(String city) {
        DatabaseReference cropRef = FirebaseDatabase.getInstance(DB_URL).getReference("pending_crops");
        Query query = cropRef.orderByChild("location").equalTo(city.toLowerCase().trim());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                int pendingCount = 0;

                // NEW CORRECTED CODE
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Crop crop = ds.getValue(Crop.class); // Now it matches requestList
                    if (crop != null) {
                        requestList.add(crop);
                    }
                }
                tvPending.setText(String.valueOf(pendingCount));

                if (requestList.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged(); // Refreshes the UI
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}