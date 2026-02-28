package com.example.agriconnect;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;

// ✅ CHANGED: Replaced LineChart imports with BarChart imports
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.agriconnect.adapters.PredictionAdapter;
import com.example.agriconnect.models.PredictionRequest;
import com.example.agriconnect.models.PredictionResponse;
import com.example.agriconnect.network.ApiClient;
import com.example.agriconnect.network.ApiService;
import com.example.agriconnect.network.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemandPredictionActivity extends AppCompatActivity {

    // UI Components
    private Spinner spinnerVegetable, spinnerDistrict;
    private TextInputEditText etDays;
    private Button btnPredict, btnFetchDistricts;
    private RecyclerView recyclerViewPredictions;
    private TextView tvNoData;
    private ProgressBar progressBar;

    // Summary card views
    private CardView cardResults;
    private TextView tvAvgDemand, tvAvgDemandLabel;
    private TextView tvTotalDemand, tvTotalDemandLabel;
    private TextView tvPeakDay, tvPeakDayLabel;
    private TextView tvMinDay, tvMinDayLabel;

    // ✅ CHANGED: BarChart instead of LineChart
    private BarChart barChart;

    // Data
    private List<String> vegetableList = new ArrayList<>();
    private List<String> districtList  = new ArrayList<>();
    private ArrayAdapter<String> vegetableAdapter;
    private ArrayAdapter<String> districtAdapter;
    private PredictionAdapter predictionAdapter;

    // API
    private ApiService apiService;

    // Selected values
    private String selectedVegetable = "";
    private String selectedDistrict  = "";

    private static final Map<String, List<String>> VEGETABLE_DISTRICTS = new HashMap<String, List<String>>() {{
        List<String> maharashtra = Arrays.asList(
                "Ahmednagar","Akola","Amarawati","Jalgaon","Kolhapur",
                "Mumbai","Nagpur","Nashik","Pune","Sambhajinagar",
                "Sangli","Satara","Thane"
        );
        put("Beetroot",     maharashtra);
        put("Brinjal",      maharashtra);
        put("Cabbage",      maharashtra);
        put("Ginger",       maharashtra);
        put("Green Chilli", maharashtra);
        put("Ladiesfinger", maharashtra);
        put("Onion",        maharashtra);
        put("Potato",       maharashtra);
        put("Pumpkin",      maharashtra);
        put("sweetpotato",  maharashtra);
        put("Tomato",       maharashtra);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand_prediction);

        initViews();
        apiService = ApiClient.getApiService();
        setupAdapters();
        loadVegetables();
        setClickListeners();
    }

    private void initViews() {
        spinnerVegetable        = findViewById(R.id.spinner_vegetable);
        spinnerDistrict         = findViewById(R.id.spinner_district);
        etDays                  = findViewById(R.id.et_days);
        btnPredict              = findViewById(R.id.btn_predict);
        btnFetchDistricts       = findViewById(R.id.btn_fetch_districts);
        recyclerViewPredictions = findViewById(R.id.recycler_predictions);
        tvNoData                = findViewById(R.id.tv_no_data);
        progressBar             = findViewById(R.id.progress_bar);

        // Summary cards
        cardResults        = findViewById(R.id.card_results);
        tvAvgDemand        = findViewById(R.id.tv_avg_demand);
        tvAvgDemandLabel   = findViewById(R.id.tv_avg_demand_label);
        tvTotalDemand      = findViewById(R.id.tv_total_demand);
        tvTotalDemandLabel = findViewById(R.id.tv_total_demand_label);
        tvPeakDay          = findViewById(R.id.tv_peak_day);
        tvPeakDayLabel     = findViewById(R.id.tv_peak_day_label);
        tvMinDay           = findViewById(R.id.tv_min_day);
        tvMinDayLabel      = findViewById(R.id.tv_min_day_label);

        // ✅ CHANGED: find bar_chart view (rename in XML too — see note below)
        barChart = findViewById(R.id.barChart);

        recyclerViewPredictions.setLayoutManager(new LinearLayoutManager(this));
        predictionAdapter = new PredictionAdapter();
        recyclerViewPredictions.setAdapter(predictionAdapter);

        cardResults.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
    }

    private void setupAdapters() {
        vegetableList.clear();
        vegetableList.add("Select Vegetable");
        districtList.clear();
        districtList.add("Select District");

        vegetableAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, vegetableList);
        vegetableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVegetable.setAdapter(vegetableAdapter);

        districtAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        spinnerVegetable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos > 0) {
                    selectedVegetable = vegetableList.get(pos);
                    loadDistricts(selectedVegetable);
                } else {
                    selectedVegetable = "";
                    districtList.clear();
                    districtList.add("Select District");
                    districtAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) { selectedVegetable = ""; }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedDistrict = (pos > 0) ? districtList.get(pos) : "";
            }
            @Override public void onNothingSelected(AdapterView<?> p) { selectedDistrict = ""; }
        });
    }

    private void setClickListeners() {
        btnFetchDistricts.setOnClickListener(v -> {
            if (selectedVegetable.isEmpty() || selectedVegetable.equals("Select Vegetable")) {
                Toast.makeText(this, "Please select a vegetable first", Toast.LENGTH_SHORT).show();
                return;
            }
            loadDistricts(selectedVegetable);
        });

        btnPredict.setOnClickListener(v -> predictDemand());
    }

    private void loadVegetables() {
        vegetableList.clear();
        vegetableList.add("Select Vegetable");
        vegetableList.add("Beetroot");
        vegetableList.add("Brinjal");
        vegetableList.add("Cabbage");
        vegetableList.add("Ginger");
        vegetableList.add("Green Chilli");
        vegetableList.add("Ladiesfinger");
        vegetableList.add("Onion");
        vegetableList.add("Potato");
        vegetableList.add("Pumpkin");
        vegetableList.add("sweetpotato");
        vegetableList.add("Tomato");
        vegetableAdapter.notifyDataSetChanged();
    }

    private void loadDistricts(String vegetable) {
        districtList.clear();
        districtList.add("Select District");
        List<String> districts = VEGETABLE_DISTRICTS.get(vegetable);
        if (districts != null) districtList.addAll(districts);
        districtAdapter.notifyDataSetChanged();
        Toast.makeText(this, districtList.size() - 1 + " districts loaded", Toast.LENGTH_SHORT).show();
    }

    private void predictDemand() {
        if (selectedVegetable.isEmpty() || selectedVegetable.equals("Select Vegetable")) {
            Toast.makeText(this, "कृपया भाजी निवडा / Please select a vegetable", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDistrict.isEmpty() || selectedDistrict.equals("Select District")) {
            Toast.makeText(this, "कृपया जिल्हा निवडा / Please select a district", Toast.LENGTH_SHORT).show();
            return;
        }

        String daysStr = etDays.getText().toString().trim();
        if (daysStr.isEmpty()) {
            Toast.makeText(this, "कृपया दिवस टाका / Please enter number of days", Toast.LENGTH_SHORT).show();
            return;
        }

        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days < 1 || days > 30) {
                Toast.makeText(this, "दिवस 1 ते 30 / Days must be 1-30", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number of days", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "इंटरनेट नाही / No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        cardResults.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
        recyclerViewPredictions.setVisibility(View.GONE);

        PredictionRequest request = new PredictionRequest(selectedVegetable, selectedDistrict, days);

        apiService.predictDemand(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    PredictionResponse result = response.body();
                    if (result.isSuccess() && result.getPredictions() != null
                            && !result.getPredictions().isEmpty()) {
                        displayResults(result);
                    } else {
                        String err = result.getError() != null ? result.getError() : "No predictions available";
                        tvNoData.setText(err);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvNoData.setText("Server error. Please try again.");
                    tvNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(DemandPredictionActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PREDICT", "Error", t);
            }
        });
    }

    private void displayResults(PredictionResponse result) {
        List<PredictionResponse.Prediction> predictions = result.getPredictions();

        // ✅ FIXED: Always "kg", never "units"
        final String unit = "kg";

        // ── Summary Cards with Trilingual Labels ───────────────────────────
        if (result.getSummary() != null) {
            Map<String, Object> s = result.getSummary();

            double avgDaily    = safeDouble(s.get("average_daily"));
            double totalDemand = safeDouble(s.get("total_demand"));
            double maxDemand   = safeDouble(s.get("max_demand"));
            double minDemand   = safeDouble(s.get("min_demand"));
            String peakDay     = s.get("peak_day")   != null ? s.get("peak_day").toString()   : "-";
            String lowestDay   = s.get("lowest_day") != null ? s.get("lowest_day").toString() : "-";

            // Value always shows "X kg"
            tvAvgDemand.setText(String.format("%.0f kg", avgDaily));
            tvTotalDemand.setText(String.format("%.0f kg", totalDemand));
            tvPeakDay.setText(peakDay + "\n" + String.format("%.0f kg", maxDemand));
            tvMinDay.setText(lowestDay + "\n" + String.format("%.0f kg", minDemand));

            // ✅ Trilingual labels: Marathi / Hindi / English
            tvAvgDemandLabel.setText("रोजची सरासरी / रोज़ औसत\nAvg Daily");
            tvTotalDemandLabel.setText("एकूण मागणी / कुल मांग\nTotal Demand");
            tvPeakDayLabel.setText("सर्वाधिक दिवस / सबसे ज़्यादा\nPeak Day");
            tvMinDayLabel.setText("सर्वात कमी / सबसे कम\nLowest Day");
        }

        cardResults.setVisibility(View.VISIBLE);

        // ── ✅ BAR CHART ───────────────────────────────────────────────────
        // Find max/min values for color-coding bars
        double maxVal = Double.MIN_VALUE;
        double minVal = Double.MAX_VALUE;
        for (PredictionResponse.Prediction p : predictions) {
            if (p.getPredictedDemand() > maxVal) maxVal = p.getPredictedDemand();
            if (p.getPredictedDemand() < minVal) minVal = p.getPredictedDemand();
        }
        final double finalMaxVal = maxVal;
        final double finalMinVal = minVal;

        List<BarEntry> barEntries = new ArrayList<>();
        List<String>   xLabels   = new ArrayList<>();

        for (int i = 0; i < predictions.size(); i++) {
            PredictionResponse.Prediction p = predictions.get(i);
            barEntries.add(new BarEntry(i, (float) p.getPredictedDemand()));

            // X-axis label: date + short day name
            String date = p.getDate();
            String label = (date != null && date.length() >= 10) ? date.substring(5) : "D" + (i + 1);
            if (p.getDayOfWeek() != null && p.getDayOfWeek().length() >= 3) {
                label += "\n" + p.getDayOfWeek().substring(0, 3);
            }
            xLabels.add(label);
        }

        // Custom BarDataSet with per-bar colors
        BarDataSet dataSet = new BarDataSet(barEntries, "मागणी / Demand (kg)") {
            @Override
            public int getColor(int index) {
                float val = getEntryForIndex(index).getY();
                if (val >= (float) finalMaxVal) return Color.parseColor("#FF8F00"); // 🟠 Peak
                if (val <= (float) finalMinVal) return Color.parseColor("#E53935"); // 🔴 Lowest
                return Color.parseColor("#2E7D32");                                  // 🟢 Normal
            }
        };

        // Show "X kg" on top of each bar
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.format("%.0f kg", barEntry.getY());
            }
        });
        dataSet.setValueTextColor(Color.parseColor("#1B5E20"));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(true);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.55f);
        barChart.setData(barData);

        // Chart appearance
        barChart.getDescription().setEnabled(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);        // simpler for farmers
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setExtraBottomOffset(16f);
        barChart.setExtraTopOffset(8f);
        barChart.animateY(1000);

        // Legend
        barChart.getLegend().setTextColor(Color.parseColor("#1B5E20"));
        barChart.getLegend().setTextSize(12f);

        // X Axis — dates at bottom
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#424242"));
        xAxis.setTextSize(9f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(predictions.size());

        // Y Axis — always shows "kg"
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.parseColor("#424242"));
        leftAxis.setGridColor(Color.parseColor("#E8F5E9"));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return (int) value + " kg";
            }
        });
        barChart.getAxisRight().setEnabled(false);

        barChart.invalidate();
        barChart.setVisibility(View.VISIBLE);

        // ── RecyclerView daily cards ───────────────────────────────────────
        predictionAdapter.setPredictions(predictions);
        recyclerViewPredictions.setVisibility(View.VISIBLE);
    }

    private double safeDouble(Object val) {
        if (val == null) return 0.0;
        try { return Double.parseDouble(val.toString()); }
        catch (Exception e) { return 0.0; }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnPredict.setEnabled(!show);
        btnFetchDistricts.setEnabled(!show);
    }
}