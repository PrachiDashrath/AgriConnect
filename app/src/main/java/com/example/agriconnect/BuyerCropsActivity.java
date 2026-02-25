package com.example.agriconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class BuyerCropsActivity extends AppCompatActivity {
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<String> cropDisplayList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyers_crop);

        listView = findViewById(R.id.listViewCrops);
        progressBar = findViewById(R.id.progressBar);
        cropDisplayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cropDisplayList);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("crops");

        // 🔹 FILTER: Firebase does the work for us
        Query approvedQuery = databaseReference.orderByChild("status").equalTo("approved");

        approvedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cropDisplayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Crop c = ds.getValue(Crop.class);
                    if (c != null) {
                        cropDisplayList.add(c.getCropName() + " | ₹" + c.getPrice());
                    }
                }
                adapter.notifyDataSetChanged();
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BuyerCropsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}