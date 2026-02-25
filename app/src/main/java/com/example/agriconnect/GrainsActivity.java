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

public class GrainsActivity extends AppCompatActivity {

    private static final int REQ_CALL_PHONE = 1;

    ListView listView;
    Button btnCall, btnSms;

    String[] grains = {"Rice", "Wheat", "Maize", "Barley", "Millet"};
    String farmerPhone = "9876543210";
    String selectedGrain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grains);

        listView = findViewById(R.id.listViewItems);
        btnCall = findViewById(R.id.btnCall);
        btnSms = findViewById(R.id.btnSms);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, grains);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedGrain = grains[position];
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", farmerPhone, null));
            smsIntent.putExtra("sms_body", "Hello, I want to buy " + selectedGrain + ".");
            startActivity(smsIntent);
        });

        btnCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + farmerPhone));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL_PHONE);
                return;
            }
            startActivity(callIntent);
        });

        btnSms.setOnClickListener(v -> {
            String item = selectedGrain != null ? selectedGrain : "grains";
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", farmerPhone, null));
            smsIntent.putExtra("sms_body", "Hello, I am interested in buying " + item + ".");
            startActivity(smsIntent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
