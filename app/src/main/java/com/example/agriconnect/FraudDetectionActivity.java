package com.example.agriconnect;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FraudDetectionActivity extends AppCompatActivity {

    private Button btnRateFarmer, btnReportFarmer;
    private RatingBar ratingBar;
    private AutoCompleteTextView autoFarmerSearch;

    private DatabaseReference farmersRef;

    private String selectedFarmerId = null;
    private List<String> farmerNames = new ArrayList<>();
    private List<String> farmerIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fraud_detection);

        btnRateFarmer = findViewById(R.id.btnRateFarmer);
        btnReportFarmer = findViewById(R.id.btnReportFarmer);
        ratingBar = findViewById(R.id.ratingBar);
        autoFarmerSearch = findViewById(R.id.autoFarmerSearch);

        // 🔥 FIXED HERE (Capital F)
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );

        farmersRef = database.getReference("Farmers");

        loadFarmers();

        btnRateFarmer.setOnClickListener(v -> submitRating());
        btnReportFarmer.setOnClickListener(v -> reportFarmer());
    }

    // 🔹 Load Farmers into AutoComplete
    private void loadFarmers() {


        farmersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                android.util.Log.d("FARMER_DEBUG", "Farmers count: " + snapshot.getChildrenCount());
                farmerNames.clear();
                farmerIds.clear();

                for (DataSnapshot farmerSnapshot : snapshot.getChildren()) {

                    String farmerId = farmerSnapshot.getKey();
                    String name = farmerSnapshot.child("name").getValue(String.class);

                    if (name != null && farmerId != null) {
                        farmerNames.add(name);
                        farmerIds.add(farmerId);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        FraudDetectionActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        farmerNames
                );

                autoFarmerSearch.setAdapter(adapter);

                autoFarmerSearch.setOnItemClickListener((parent, view, position, id) -> {
                    selectedFarmerId = farmerIds.get(position);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FraudDetectionActivity.this,
                        "Failed to load farmers",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 Submit Rating
    private void submitRating() {

        if (selectedFarmerId == null) {
            Toast.makeText(this, "Please select a farmer first", Toast.LENGTH_SHORT).show();
            return;
        }

        int givenStars = (int) ratingBar.getRating();

        if (givenStars == 0) {
            Toast.makeText(this, "Please select rating first", Toast.LENGTH_SHORT).show();
            return;
        }

        farmersRef.child(selectedFarmerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Integer totalStars = snapshot.child("totalStars").getValue(Integer.class);
                        Integer totalRatings = snapshot.child("totalRatings").getValue(Integer.class);
                        Integer reportCount = snapshot.child("reportCount").getValue(Integer.class);

                        if (totalStars == null) totalStars = 0;
                        if (totalRatings == null) totalRatings = 0;
                        if (reportCount == null) reportCount = 0;

                        totalStars += givenStars;
                        totalRatings++;

                        double finalRating = FraudDetectionModel.calculateFinalRating(
                                totalStars,
                                totalRatings,
                                reportCount
                        );

                        farmersRef.child(selectedFarmerId).child("totalStars").setValue(totalStars);
                        farmersRef.child(selectedFarmerId).child("totalRatings").setValue(totalRatings);
                        farmersRef.child(selectedFarmerId).child("finalRating").setValue(finalRating);

                        Toast.makeText(FraudDetectionActivity.this,
                                "Rating submitted successfully!",
                                Toast.LENGTH_SHORT).show();

                        ratingBar.setRating(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FraudDetectionActivity.this,
                                "Failed to submit rating",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 🔹 Report Farmer
    private void reportFarmer() {

        if (selectedFarmerId == null) {
            Toast.makeText(this, "Please select a farmer first", Toast.LENGTH_SHORT).show();
            return;
        }

        farmersRef.child(selectedFarmerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Integer totalStars = snapshot.child("totalStars").getValue(Integer.class);
                        Integer totalRatings = snapshot.child("totalRatings").getValue(Integer.class);
                        Integer reportCount = snapshot.child("reportCount").getValue(Integer.class);

                        if (totalStars == null) totalStars = 0;
                        if (totalRatings == null) totalRatings = 0;
                        if (reportCount == null) reportCount = 0;

                        reportCount++;

                        double finalRating = FraudDetectionModel.calculateFinalRating(
                                totalStars,
                                totalRatings,
                                reportCount
                        );

                        farmersRef.child(selectedFarmerId).child("reportCount").setValue(reportCount);
                        farmersRef.child(selectedFarmerId).child("finalRating").setValue(finalRating);

                        Toast.makeText(FraudDetectionActivity.this,
                                "Farmer reported successfully!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FraudDetectionActivity.this,
                                "Failed to report farmer",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}