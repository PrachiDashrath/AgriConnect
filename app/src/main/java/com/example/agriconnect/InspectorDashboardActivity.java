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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class InspectorDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InspectorAdapter adapter;
    private List<Crop> cropList;
    private LinearLayout emptyState;
    private TextView tvLocation, tvPendingCount, tvApprovedCount, tvRejectedCount;
    private ImageButton btnChatbotCorner;
    private String inspectorCity = "";
    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector_dashboard);

        // Initialize UI
        recyclerView = findViewById(R.id.recyclerInspectionRequests);
        emptyState = findViewById(R.id.emptyStateLayout);
        tvLocation = findViewById(R.id.tvInspectorLocation);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvApprovedCount = findViewById(R.id.tvApprovedCount);
        tvRejectedCount = findViewById(R.id.tvRejectedCount);
        btnChatbotCorner = findViewById(R.id.btnChatbotCorner);

        cropList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InspectorAdapter(this, cropList);
        recyclerView.setAdapter(adapter);

        // Chatbot Action
        btnChatbotCorner.setOnClickListener(v -> {
            Intent intent = new Intent(InspectorDashboardActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });

        fetchInspectorProfile();
    }

    private void fetchInspectorProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            inspectorCity = snapshot.child("location").getValue(String.class);
                            if (inspectorCity != null) {
                                tvLocation.setText("📍 Location: " + inspectorCity);
                                loadCropsByLocation();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadCropsByLocation() {
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference("pending_crops");
        Query query = ref.orderByChild("location").equalTo(inspectorCity);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cropList.clear();
                int pending = 0, approved = 0, rejected = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Crop crop = ds.getValue(Crop.class);
                    if (crop != null) {
                        if ("pending".equalsIgnoreCase(crop.getStatus())) {
                            cropList.add(crop);
                            pending++;
                        } else if ("approved".equalsIgnoreCase(crop.getStatus())) {
                            approved++;
                        } else if ("rejected".equalsIgnoreCase(crop.getStatus())) {
                            rejected++;
                        }
                    }
                }

                tvPendingCount.setText(String.valueOf(pending));
                tvApprovedCount.setText(String.valueOf(approved));
                tvRejectedCount.setText(String.valueOf(rejected));

                if (cropList.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InspectorDashboardActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}