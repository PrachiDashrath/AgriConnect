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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BuyerFormActivity extends AppCompatActivity {

    private EditText etName, etContact, etCity;
    private ImageButton btnMicBuyerName, btnMicBuyerCity;
    private Button btnSubmit;
    private ProgressBar progressBar;
    // Keep your specific DB URL
    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_form);

        // 1. Initialize UI
        etName = findViewById(R.id.etBuyerName);
        etContact = findViewById(R.id.etBuyerContact);
        etCity = findViewById(R.id.etBuyerCity);
        btnSubmit = findViewById(R.id.btnSubmitBuyer);
        progressBar = findViewById(R.id.progressBar);
        btnMicBuyerName = findViewById(R.id.btnMicName);
        btnMicBuyerCity = findViewById(R.id.btnMicCity);

        // 2. Voice Input Setup
        btnMicBuyerName.setOnClickListener(v -> startVoiceInput(101));
        btnMicBuyerCity.setOnClickListener(v -> startVoiceInput(102));

        // 3. Submit Action
        btnSubmit.setOnClickListener(v -> submitBuyerForm());
    }

    private void startVoiceInput(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(this, "Voice recognition not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (res != null && !res.isEmpty()) {
                if (requestCode == 101) etName.setText(res.get(0));
                if (requestCode == 102) etCity.setText(res.get(0));
            }
        }
    }

    private void submitBuyerForm() {
        String name = etName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create User Object
        Map<String, Object> buyer = new HashMap<>();
        buyer.put("uid", uid);
        buyer.put("name", name);
        buyer.put("contact", contact);
        buyer.put("location", city.toLowerCase());
        buyer.put("userType", "Buyer");

        // Save to Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid);
        dbRef.setValue(buyer).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BuyerFormActivity.this, "Profile Setup Complete!", Toast.LENGTH_SHORT).show();

                // FIXED FLOW: Redirect to BuyerDashboardActivity
                Intent intent = new Intent(BuyerFormActivity.this, BuyerDashboardActivity.class);

                // Clear the backstack so user can't back-button into the form again
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(BuyerFormActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}