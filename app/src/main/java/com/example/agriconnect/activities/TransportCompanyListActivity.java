package com.example.agriconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.agriconnect.R;
import com.example.agriconnect.adapters.TransportAdapter;
import com.example.agriconnect.models.Transporter;

import java.util.ArrayList;
import java.util.List;

public class TransportCompanyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransportAdapter adapter;
    private List<Transporter> transportList;
    private List<Transporter> filteredList;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private EditText etSearch;
    private ImageButton btnBack;
    private LinearLayout emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_company_list);

        initViews();
        setupRecyclerView();
        loadTransporters();
        setupSearch();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvTransportCompanies);
        progressBar  = findViewById(R.id.progressBar);
        etSearch     = findViewById(R.id.etSearch);
        btnBack      = findViewById(R.id.btnBack);
        emptyState   = findViewById(R.id.emptyState);

        btnBack.setOnClickListener(v -> finish());

        transportList = new ArrayList<>();
        filteredList  = new ArrayList<>();

        try {
            mDatabase = FirebaseDatabase.getInstance(
                            "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("transporters");
        } catch (Exception e) {
            Toast.makeText(this, "Firebase error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransportAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        // Direct touch listener — completely bypasses adapter clicks
        final GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv,
                                                 @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    if (position != RecyclerView.NO_POSITION
                            && position < filteredList.size()) {

                        Transporter t = filteredList.get(position);
                        android.util.Log.d("TOUCH_CLICK",
                                "Tapped position=" + position + " name=" + t.getName());

                        Intent intent = new Intent(TransportCompanyListActivity.this,
                                TransporterDetailActivity.class);
                        intent.putExtra("transporterId",       t.getId());
                        intent.putExtra("transporterName",     t.getName());
                        intent.putExtra("transporterVehicle",  t.getVehicleType());
                        intent.putExtra("transporterContact",  t.getContact());
                        intent.putExtra("transporterLocation", t.getLocation());
                        intent.putExtra("transporterPrice",    t.getPricePerKm());
                        intent.putExtra("transporterVerified", t.isVerified());
                        intent.putExtra("transporterRating",   t.getRating());
                        intent.putExtra("transporterGst",      t.getGstNumber());
                        intent.putExtra("transporterLicense",  t.getLicenseNumber());
                        intent.putExtra("transporterTotalTrips", t.getTotalTrips());
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv,
                                     @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {}
        });
    }

    private void loadTransporters() {
        if (mDatabase == null) {
            progressBar.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transportList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        Transporter t = ds.getValue(Transporter.class);
                        if (t != null) {
                            t.setId(ds.getKey());
                            transportList.add(t);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                filteredList.clear();
                filteredList.addAll(transportList);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (transportList.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                Toast.makeText(TransportCompanyListActivity.this,
                        "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(transportList);
        } else {
            String query = text.toLowerCase();
            for (Transporter t : transportList) {
                if ((t.getName() != null && t.getName().toLowerCase().contains(query)) ||
                        (t.getLocation() != null && t.getLocation().toLowerCase().contains(query)) ||
                        (t.getVehicleType() != null && t.getVehicleType().toLowerCase().contains(query))) {
                    filteredList.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}