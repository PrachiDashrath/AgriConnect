package com.example.agriconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class MarketPriceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MarketPriceAdapter adapter;
    private List<MarketPrice> marketList;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_price);

        // UI Initialization - Ensure ID matches your XML
        recyclerView = findViewById(R.id.rvMarketPrice);
        progressBar = findViewById(R.id.pbMarket);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        marketList = new ArrayList<>();
        adapter = new MarketPriceAdapter(this, marketList);
        recyclerView.setAdapter(adapter);

        // DATABASE REFERENCE
        mDatabase = FirebaseDatabase.getInstance("https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("market_prices");

        fetchMarketData();
    }

    private void fetchData() {
        // Renamed to match your internal call if necessary
        fetchMarketData();
    }

    private void fetchMarketData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                marketList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        MarketPrice item = ds.getValue(MarketPrice.class);
                        if (item != null) {
                            marketList.add(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(MarketPriceActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}