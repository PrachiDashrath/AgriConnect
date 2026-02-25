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

public class FarmerFormActivity extends AppCompatActivity {

    private EditText etName, etContact, etCity;
    private ImageButton btnMicName, btnMicCity;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_form);

        etName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etContact);
        etCity = findViewById(R.id.etCity);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        btnMicName = findViewById(R.id.btnMicName);
        btnMicCity = findViewById(R.id.btnMicCity);

        btnMicName.setOnClickListener(v -> startVoiceInput(101));
        btnMicCity.setOnClickListener(v -> startVoiceInput(102));
        btnSubmit.setOnClickListener(v -> performSubmission());
    }

    private void startVoiceInput(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                if (requestCode == 101) etName.setText(result.get(0));
                if (requestCode == 102) etCity.setText(result.get(0));
            }
        }
    }

    private void performSubmission() {
        String name = etName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("name", name);
        data.put("contact", contact);
        data.put("location", city.toLowerCase());
        data.put("userType", "Farmer");

        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid);
        dbRef.setValue(data).addOnCompleteListener(task -> {
            startActivity(new Intent(this, FarmerDashboardActivity.class));
            finish();
        });
    }
}