package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddCropActivity extends AppCompatActivity {

    private EditText etCropName, etCategory, etPrice, etQuantity;
    private ImageButton btnMicName, btnMicCategory;
    private Button btnAddCrop;
    private ProgressBar progressBar;
    private String farmerLocation = "unknown";
    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        etCropName = findViewById(R.id.etCropName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        btnAddCrop = findViewById(R.id.btnAddCrop);
        progressBar = findViewById(R.id.progressBar);
        btnMicName = findViewById(R.id.btnMicName);
        btnMicCategory = findViewById(R.id.btnMicCategory);

        fetchFarmerLocation();

        btnMicName.setOnClickListener(v -> startVoiceInput(101));
        btnMicCategory.setOnClickListener(v -> startVoiceInput(102));
        btnAddCrop.setOnClickListener(v -> saveCrop());
    }

    private void fetchFarmerLocation() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Check "location" field (from FarmerForm)
                            Object loc = snapshot.child("location").getValue();
                            if (loc != null) {
                                farmerLocation = loc.toString().toLowerCase().trim();
                            }
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
            }
        }
    }

    private void saveCrop() {
        String name = etCropName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter crop name", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("pending_crops");
        String cropId = dbRef.push().getKey();

        Map<String, Object> cropMap = new HashMap<>();
        cropMap.put("cropId", cropId);
        cropMap.put("cropName", name);
        cropMap.put("category", etCategory.getText().toString());
        cropMap.put("price", etPrice.getText().toString());
        cropMap.put("quantity", etQuantity.getText().toString());
        cropMap.put("location", farmerLocation); // Sent to Inspector's area
        cropMap.put("status", "pending");
        cropMap.put("farmerId", FirebaseAuth.getInstance().getUid());

        dbRef.child(cropId).setValue(cropMap).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Crop sent to local inspector!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}