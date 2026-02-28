package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class MarketPlaceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MarketPriceAdapter adapter;
    private List<MarketPrice> approvedList; // List currently shown
    private List<MarketPrice> fullList;     // Master copy of all data
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private TextView tvLocationSubtitle;
    private ImageButton btnBack;

    // Filter Views
    private SearchView searchView;
    private Spinner locationSpinner;
    private String[] cityFilter = {
            "All Locations",
            "Pune", "Mumbai", "Nagpur", "Nashik", "Thane", "Aurangabad", "Solapur",
            "Ahmednagar", "Akola", "Amravati", "Beed", "Bhandara", "Buldhana",
            "Chandrapur", "Dhule", "Gadchiroli", "Gondia", "Hingoli", "Jalgaon",
            "Jalna", "Kolhapur", "Latur", "Nandurbar", "Nanded", "Osmanabad",
            "Palghar", "Parbhani", "Raigad", "Ratnagiri", "Sangli", "Satara",
            "Sindhudurg", "Wardha", "Washim", "Yavatmal"
    };

    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);

        // 1. Initialize Views
        recyclerView = findViewById(R.id.recyclerMarketplace);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        tvLocationSubtitle = findViewById(R.id.tvLocationSubtitle);
        searchView = findViewById(R.id.searchView);
        locationSpinner = findViewById(R.id.locationSpinner);
        btnBack = findViewById(R.id.btnBack);

        // 2. Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvedList = new ArrayList<>();
        fullList = new ArrayList<>();
        adapter = new MarketPriceAdapter(this, approvedList);
        recyclerView.setAdapter(adapter);

        // 3. Setup Spinner Filter
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityFilter);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(spinAdapter);

        // 4. FIXED BACK BUTTON LOGIC
        btnBack.setOnClickListener(v -> {
            // Redirect explicitly to BuyerDashboardActivity
            Intent intent = new Intent(MarketPlaceActivity.this, BuyerDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // 5. Fetch Data (Initial Load)
        loadAllCropsFromFirebase();

        // 6. Setup Search Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters(newText, locationSpinner.getSelectedItem().toString());
                return true;
            }
        });

        // 7. Setup Spinner Listener
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                applyFilters(searchView.getQuery().toString(), cityFilter[i]);
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void loadAllCropsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference("market_prices");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        MarketPrice item = ds.getValue(MarketPrice.class);
                        if (item != null) fullList.add(item);
                    }
                }
                applyFilters("", "All Locations");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void applyFilters(String query, String city) {
        approvedList.clear();
        for (MarketPrice item : fullList) {
            boolean matchesSearch = item.cropName.toLowerCase().contains(query.toLowerCase());
            boolean matchesCity = city.equals("All Locations") || item.location.equalsIgnoreCase(city);

            if (matchesSearch && matchesCity) {
                approvedList.add(item);
            }
        }
        updateUI();
    }

    private void updateUI() {
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
}