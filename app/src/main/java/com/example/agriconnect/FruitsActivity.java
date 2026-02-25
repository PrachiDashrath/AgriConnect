package com.example.agriconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class FruitsActivity extends AppCompatActivity {

    private static final int REQ_CALL_PHONE = 1;

    ListView listView;
    Button btnCall, btnSms;

    // Sample fruit list
    String[] fruits = {"Mango", "Banana", "Apple", "Papaya", "Grapes", "Orange", "Pineapple"};

    // Dummy farmer phone number
    String farmerPhone = "9876543210";

    // Last-selected fruit (used when user uses Call/SMS after selecting)
    String selectedFruit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruits);

        listView = findViewById(R.id.listViewFruits);
        btnCall = findViewById(R.id.btnCall);
        btnSms = findViewById(R.id.btnSms);

        // Set adapter for fruit list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, fruits);
        listView.setAdapter(adapter);

        // When buyer taps a fruit in the list -> open SMS app prefilled for that fruit
        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedFruit = fruits[position];
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", farmerPhone, null));
            smsIntent.putExtra("sms_body", "Hello, I want to buy " + selectedFruit + ".");
            startActivity(smsIntent);
        });

        // Call Button Click: attempt direct call (requires CALL_PHONE permission)
        btnCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + farmerPhone));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // request permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL_PHONE);
                return;
            }
            startActivity(callIntent);
        });

        // SMS Button Click: opens SMS app (no runtime SEND_SMS permission needed when using ACTION_VIEW)
        btnSms.setOnClickListener(v -> {
            String fruit = selectedFruit != null ? selectedFruit : "fruits";
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", farmerPhone, null));
            smsIntent.putExtra("sms_body", "Hello, I am interested in buying " + fruit + ".");
            startActivity(smsIntent);
        });
    }

    /**
     * Handle permission result. IMPORTANT: call super(...) first so the framework can also process.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // <— required: call super first to satisfy framework/IDE and avoid that warning
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted — start the call
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + farmerPhone));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                }
            } else {
                Toast.makeText(this, "Call Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
