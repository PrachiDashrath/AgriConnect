package com.example.agriconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        RecyclerView rvWeather = findViewById(R.id.rvWeather);
        rvWeather.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<Weather> weatherList = new ArrayList<>();
        weatherList.add(new Weather("Mon", "Sunny", "30°C"));
        weatherList.add(new Weather("Tue", "Rainy", "25°C"));
        weatherList.add(new Weather("Wed", "Cloudy", "28°C"));
        weatherList.add(new Weather("Thu", "Sunny", "32°C"));
        weatherList.add(new Weather("Fri", "Rainy", "26°C"));

        WeatherAdapter adapter = new WeatherAdapter(weatherList);
        rvWeather.setAdapter(adapter);
    }
}
