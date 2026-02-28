package com.example.agriconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CropManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CropAdapter adapter; // Assuming you have a CropAdapter
    private List<Crop> cropList;
    private DatabaseReference mDatabase;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_management);

        // 1. Initialize Firebase
        currentUserId = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Approved_Crops");

        // 2. Initialize UI Components
        recyclerView = findViewById(R.id.recyclerViewCrops);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cropList = new ArrayList<>();
        adapter = new CropAdapter(this, cropList);
        recyclerView.setAdapter(adapter);

        // 3. Setup Professional Links


        // 4. Load User's Crops
        loadUserCrops();
    }



    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void loadUserCrops() {
        // Only fetch crops belonging to the logged-in Farmer
        mDatabase.orderByChild("farmerId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cropList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Crop crop = ds.getValue(Crop.class);
                            if (crop != null) {
                                cropList.add(crop);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CropManagementActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}