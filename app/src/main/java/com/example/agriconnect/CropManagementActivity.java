package com.example.agriconnect;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CropManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CropAdapter adapter;
    private List<Crop> cropList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_management);

        recyclerView = findViewById(R.id.recyclerViewCrops);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCropsFromDB();
    }

    private void loadCropsFromDB() {
        cropList = DatabaseHelper.getCrops(); // ✅ static fetch

        if (cropList.isEmpty()) {
            Toast.makeText(this, "No crops available. Please add crops.", Toast.LENGTH_SHORT).show();
        }

        adapter = new CropAdapter(this, cropList);
        recyclerView.setAdapter(adapter);
    }
}
