package com.example.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agriconnect.adapters.PredictionAdapter;
import com.example.agriconnect.models.PredictionRequest;
import com.example.agriconnect.models.PredictionResponse;
import com.example.agriconnect.models.VegetableResponse;
import com.example.agriconnect.network.ApiClient;
import com.example.agriconnect.network.ApiService;
import com.example.agriconnect.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoiceDemandActivity extends AppCompatActivity {

    // UI Components
    private Spinner spinnerLanguage, spinnerVegetable;
    private Button btnSpeak, btnGetPredictions;
    private TextView tvSelectedInfo, tvStatus, tvResult;
    private LinearLayout layoutVoiceInput, layoutManualInput;
    private CardView cardVoice, cardManual;
    private RecyclerView recyclerViewPredictions;
    private ProgressBar progressBar;
    private ImageView micIcon;

    // Data
    private List<String> vegetableList = new ArrayList<>();
    private ArrayAdapter<String> vegetableAdapter;
    private PredictionAdapter predictionAdapter;
    private ApiService apiService;

    // Selected values
    private String selectedVegetable = "";
    private String selectedDistrict = "Ahmednagar"; // Default district
    private String selectedLanguage = "English";

    // Voice Recognition
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_demand);

        initViews();
        setupLanguageSpinner();
        setupVegetableSpinner();

        apiService = ApiClient.getApiService();

        loadVegetables();
        setupClickListeners();
        initVoiceRecognition();
    }

    private void initViews() {
        spinnerLanguage = findViewById(R.id.spinner_language);
        spinnerVegetable = findViewById(R.id.spinner_vegetable);
        btnSpeak = findViewById(R.id.btn_speak);
        btnGetPredictions = findViewById(R.id.btn_get_predictions);
        tvSelectedInfo = findViewById(R.id.tv_selected_info);
        tvStatus = findViewById(R.id.tv_status);
        tvResult = findViewById(R.id.tv_result);
        layoutVoiceInput = findViewById(R.id.layout_voice_input);
        layoutManualInput = findViewById(R.id.layout_manual_input);
        cardVoice = findViewById(R.id.card_voice);
        cardManual = findViewById(R.id.card_manual);
        recyclerViewPredictions = findViewById(R.id.recycler_predictions);
        progressBar = findViewById(R.id.progress_bar);
        micIcon = findViewById(R.id.mic_icon);

        // Setup RecyclerView
        recyclerViewPredictions.setLayoutManager(new LinearLayoutManager(this));
        predictionAdapter = new PredictionAdapter();
        recyclerViewPredictions.setAdapter(predictionAdapter);
    }

    private void setupLanguageSpinner() {
        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("हिंदी");
        languages.add("मराठी");

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = languages.get(position);
                updateUILanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupVegetableSpinner() {
        vegetableAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, vegetableList);
        vegetableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVegetable.setAdapter(vegetableAdapter);

        spinnerVegetable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < vegetableList.size()) {
                    selectedVegetable = vegetableList.get(position);
                    tvSelectedInfo.setText(String.format("Model available for %s in %s",
                            selectedVegetable, selectedDistrict));
                    tvStatus.setText(String.format("Analyzing: %s in %s",
                            selectedVegetable, selectedDistrict));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedVegetable = "";
            }
        });
    }

    private void updateUILanguage() {
        // Update UI text based on selected language
        switch (selectedLanguage) {
            case "हिंदी":
                btnSpeak.setText("बोलें");
                btnGetPredictions.setText("भविष्यवाणी प्राप्त करें");
                break;
            case "मराठी":
                btnSpeak.setText("बोला");
                btnGetPredictions.setText("अंदाज मिळवा");
                break;
            default:
                btnSpeak.setText("SPEAK");
                btnGetPredictions.setText("Get Predictions");
                break;
        }
    }

    private void setupClickListeners() {
        btnSpeak.setOnClickListener(v -> startVoiceInput());

        btnGetPredictions.setOnClickListener(v -> {
            if (selectedVegetable.isEmpty() || selectedVegetable.equals("Select Vegetable")) {
                Toast.makeText(this, "Please select a vegetable", Toast.LENGTH_SHORT).show();
                return;
            }
            predictDemand();
        });

        cardVoice.setOnClickListener(v -> {
            layoutVoiceInput.setVisibility(View.VISIBLE);
            layoutManualInput.setVisibility(View.GONE);
        });

        cardManual.setOnClickListener(v -> {
            layoutVoiceInput.setVisibility(View.GONE);
            layoutManualInput.setVisibility(View.VISIBLE);
        });
    }

    private void initVoiceRecognition() {
        // Check if speech recognition is available
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak vegetable name...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Voice input not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                matchVegetableFromVoice(spokenText);
            }
        }
    }

    private void matchVegetableFromVoice(String spokenText) {
        // Simple matching - find vegetable in spoken text
        spokenText = spokenText.toLowerCase();

        for (int i = 1; i < vegetableList.size(); i++) {
            String vegetable = vegetableList.get(i).toLowerCase();
            if (spokenText.contains(vegetable)) {
                final int position = i;
                runOnUiThread(() -> {
                    spinnerVegetable.setSelection(position);
                    Toast.makeText(this, "Selected: " + vegetableList.get(position),
                            Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }

        Toast.makeText(this, "Vegetable not recognized. Please select manually.",
                Toast.LENGTH_SHORT).show();
    }

    private void loadVegetables() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            loadDummyVegetables();
            return;
        }

        showLoading(true);

        Call<VegetableResponse> call = apiService.getVegetables();
        call.enqueue(new Callback<VegetableResponse>() {
            @Override
            public void onResponse(Call<VegetableResponse> call, Response<VegetableResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    VegetableResponse vegResponse = response.body();

                    if (vegResponse.isSuccess() && vegResponse.getVegetables() != null) {
                        vegetableList.clear();
                        vegetableList.add("Select Vegetable");

                        for (VegetableResponse.Vegetable veg : vegResponse.getVegetables()) {
                            vegetableList.add(veg.getName());
                        }

                        runOnUiThread(() -> {
                            vegetableAdapter.notifyDataSetChanged();
                            Toast.makeText(VoiceDemandActivity.this,
                                    "Loaded " + (vegetableList.size() - 1) + " vegetables",
                                    Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        loadDummyVegetables();
                    }
                } else {
                    loadDummyVegetables();
                }
            }

            @Override
            public void onFailure(Call<VegetableResponse> call, Throwable t) {
                showLoading(false);
                loadDummyVegetables();
            }
        });
    }

    private void loadDummyVegetables() {
        runOnUiThread(() -> {
            vegetableList.clear();
            vegetableList.add("Select Vegetable");
            vegetableList.add("Beetroot");
            vegetableList.add("Tomato");
            vegetableList.add("Potato");
            vegetableList.add("Onion");
            vegetableList.add("Cabbage");
            vegetableList.add("Carrot");
            vegetableAdapter.notifyDataSetChanged();
            tvSelectedInfo.setText("Model available for Beetroot in Ahmednagar");
            tvStatus.setText("Analyzing: Beetroot in Ahmednagar");
        });
    }

    private void predictDemand() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            showDemoPrediction();
            return;
        }

        showLoading(true);
        tvResult.setVisibility(View.GONE);
        recyclerViewPredictions.setVisibility(View.GONE);

        PredictionRequest request = new PredictionRequest(
                selectedVegetable, selectedDistrict, 7
        );

        Call<PredictionResponse> call = apiService.predictDemand(request);
        call.enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    PredictionResponse result = response.body();

                    if (result.isSuccess() && result.getPredictions() != null && !result.getPredictions().isEmpty()) {
                        predictionAdapter.setPredictions(result.getPredictions());
                        recyclerViewPredictions.setVisibility(View.VISIBLE);

                        double total = result.getSummary() != null && result.getSummary().containsKey("total_demand") ?
                                (double) result.getSummary().get("total_demand") : 0;
                        tvResult.setText(String.format("Predicted Demand: %.0f %s", total, result.getUnit()));
                        tvResult.setVisibility(View.VISIBLE);
                    } else {
                        showDemoPrediction();
                    }
                } else {
                    showDemoPrediction();
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                showLoading(false);
                showDemoPrediction();
            }
        });
    }

    private void showDemoPrediction() {
        runOnUiThread(() -> {
            tvResult.setText("Predicted Demand: 2,450 kg");
            tvResult.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Showing demo predictions", Toast.LENGTH_SHORT).show();
        });
    }

    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
                btnGetPredictions.setEnabled(false);
                btnSpeak.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                btnGetPredictions.setEnabled(true);
                btnSpeak.setEnabled(true);
            }
        });
    }
}