package com.example.agriconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GovtSchemeActivity extends AppCompatActivity {

    RecyclerView rvSchemes;
    SchemeAdapter adapter;
    List<Scheme> schemeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_govt_scheme);

        rvSchemes = findViewById(R.id.rvSchemes);
        rvSchemes.setLayoutManager(new LinearLayoutManager(this));

        // Sample schemes (replace URLs with actual Mahadbt URLs)
        schemeList = new ArrayList<>();
        schemeList.add(new Scheme("Krishi Sinchai Yojana", "Irrigation scheme for farmers", "https://mahadbt.maharashtra.gov.in/"));
        schemeList.add(new Scheme("Mahatma Phule Krushi Yojana", "Financial aid for crop improvement", "https://mahadbt.maharashtra.gov.in/"));
        schemeList.add(new Scheme("Shetkari Credit Card Scheme", "Credit support for farmers", "https://mahadbt.maharashtra.gov.in/"));

        adapter = new SchemeAdapter(this, schemeList);
        rvSchemes.setAdapter(adapter);
    }
}
