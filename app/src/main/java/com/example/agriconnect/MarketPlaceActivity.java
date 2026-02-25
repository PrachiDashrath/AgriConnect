package com.example.agriconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class MarketPlaceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MarketPriceAdapter adapter; // Using MarketPriceAdapter
    private List<MarketPrice> approvedList; // Using MarketPrice list
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);

        recyclerView = findViewById(R.id.recyclerMarketplace);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvedList = new ArrayList<>();

        // Use the MarketPriceAdapter
        adapter = new MarketPriceAdapter(this, approvedList);
        recyclerView.setAdapter(adapter);

        loadApprovedCrops();
    }

    private void loadApprovedCrops() {
        progressBar.setVisibility(View.VISIBLE);
        // PATH MUST MATCH INSPECTOR: "market_prices"
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference("market_prices");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                approvedList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        // FETCH AS MarketPrice.class
                        MarketPrice item = ds.getValue(MarketPrice.class);
                        if (item != null) {
                            approvedList.add(item);
                        }
                    }
                }

                progressBar.setVisibility(View.GONE);
                if (approvedList.isEmpty()) {
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
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}