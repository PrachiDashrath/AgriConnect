package com.example.agriconnect.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agriconnect.R;
import com.example.agriconnect.UserSelectionActivity;
import com.example.agriconnect.models.Transporter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TransporterRegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etContact, etLoc, etVehicle, etPrice;
    private Button btnSave;
    private DatabaseReference mDatabase;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter_register);

        // Initialize UI Elements
        etName = findViewById(R.id.etTransCompanyName);
        etContact = findViewById(R.id.etTransContact);
        etLoc = findViewById(R.id.etTransLocation);
        etVehicle = findViewById(R.id.etTransVehicle);
        etPrice = findViewById(R.id.etTransPrice);
        btnSave = findViewById(R.id.btnSaveTransporter);

        // Setup Loader
        loader = new ProgressDialog(this);
        loader.setMessage("Registering your company...");
        loader.setCancelable(false);

        // Initialize Firebase - Use your specific Database URL
        mDatabase = FirebaseDatabase.getInstance("https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("transporters");

        btnSave.setOnClickListener(v -> {
            saveTransporterData();
        });
    }

    private void saveTransporterData() {
        String name = etName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String loc = etLoc.getText().toString().trim();
        String vehicle = etVehicle.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        // Validation
        if (name.isEmpty() || contact.isEmpty() || loc.isEmpty() || vehicle.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        loader.show();

        // Generate a unique ID for this transporter
        String id = mDatabase.push().getKey();
        Transporter transporter = new Transporter(name, vehicle, contact, loc, price);

        // Set additional fields if needed
        transporter.setId(id);
        transporter.setVerified(false);
        transporter.setRating(0.0);
        transporter.setTotalTrips(0);

        if (id != null) {
            mDatabase.child(id).setValue(transporter)
                    .addOnSuccessListener(aVoid -> {
                        loader.dismiss();
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                        // REDIRECTION: This moves you back to the role selection
                        Intent intent = new Intent(TransporterRegisterActivity.this, UserSelectionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish(); // Closes this screen
                    })
                    .addOnFailureListener(e -> {
                        loader.dismiss();
                        Toast.makeText(this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            loader.dismiss();
            Toast.makeText(this, "Error generating ID", Toast.LENGTH_SHORT).show();
        }
    }
}