package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddCropActivity extends AppCompatActivity {

    private EditText etCropName, etCategory, etPrice, etQuantity, etLocation;
    private ImageButton btnMicName, btnMicCategory, btnMicLocation;
    private Button btnAddCrop;
    private ProgressBar progressBar;

    private String farmerName = "Unknown", farmerContact = "N/A";
    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        etCropName = findViewById(R.id.etCropName);
        etCategory = findViewById(R.id.etCategory);
        etLocation = findViewById(R.id.etLocation);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        btnAddCrop = findViewById(R.id.btnAddCrop);
        progressBar = findViewById(R.id.progressBar);

        btnMicName = findViewById(R.id.btnMicName);
        btnMicCategory = findViewById(R.id.btnMicCategory);
        btnMicLocation = findViewById(R.id.btnMicLocation);

        fetchFarmerProfile();

        btnMicName.setOnClickListener(v -> startVoiceInput(101));
        btnMicCategory.setOnClickListener(v -> startVoiceInput(102));
        btnMicLocation.setOnClickListener(v -> startVoiceInput(103));

        btnAddCrop.setOnClickListener(v -> saveCrop());
    }

    private void fetchFarmerProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            farmerName = snapshot.child("name").getValue(String.class);
                            farmerContact = snapshot.child("contact").getValue(String.class);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void startVoiceInput(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try { startActivityForResult(intent, requestCode); }
        catch (Exception e) { Toast.makeText(this, "Mic error", Toast.LENGTH_SHORT).show(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (res != null && !res.isEmpty()) {
                if (requestCode == 101) etCropName.setText(res.get(0));
                if (requestCode == 102) etCategory.setText(res.get(0));
                if (requestCode == 103) etLocation.setText(res.get(0));
            }
        }
    }

    private void saveCrop() {
        String name = etCropName.getText().toString().trim();
        String cat = etCategory.getText().toString().trim();
        String loc = etLocation.getText().toString().trim();
        String pr = etPrice.getText().toString().trim();
        String qty = etQuantity.getText().toString().trim();

        if (name.isEmpty() || loc.isEmpty() || cat.isEmpty() || pr.isEmpty() || qty.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("pending_crops");
        String cropId = dbRef.push().getKey();

        Map<String, Object> cropMap = new HashMap<>();
        cropMap.put("cropId", cropId);
        cropMap.put("cropName", name);
        cropMap.put("category", cat);
        cropMap.put("location", loc.toLowerCase().trim());
        cropMap.put("price", pr);
        cropMap.put("quantity", qty);
        cropMap.put("status", "pending");
        cropMap.put("farmerId", FirebaseAuth.getInstance().getUid());
        cropMap.put("farmerName", farmerName);
        cropMap.put("farmerPhone", farmerContact);

        if (cropId != null) {
            dbRef.child(cropId).setValue(cropMap).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Crop listed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}