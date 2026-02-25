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

public class InspectorFormActivity extends AppCompatActivity {

    private EditText etName, etContact, etCity;
    private ImageButton btnMicInspName, btnMicInspCity;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector_form);

        etName = findViewById(R.id.etInspectorName);
        etContact = findViewById(R.id.etInspectorContact);
        etCity = findViewById(R.id.etInspectorCity);
        btnSubmit = findViewById(R.id.btnSubmitInspector);
        progressBar = findViewById(R.id.progressBar);

        btnMicInspName = findViewById(R.id.btnMicName);
        btnMicInspCity = findViewById(R.id.btnMicCity);

        btnMicInspName.setOnClickListener(v -> startVoiceInput(101));
        btnMicInspCity.setOnClickListener(v -> startVoiceInput(102));

        btnSubmit.setOnClickListener(v -> submitForm());
    }

    private void startVoiceInput(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, requestCode);
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

    private void submitForm() {
        String name = etName.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (name.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        String uid = FirebaseAuth.getInstance().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("name", name);
        data.put("location", city.toLowerCase());
        data.put("userType", "Inspector");

        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid);
        dbRef.setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(InspectorFormActivity.this, InspectorDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
            }
        });
    }
}