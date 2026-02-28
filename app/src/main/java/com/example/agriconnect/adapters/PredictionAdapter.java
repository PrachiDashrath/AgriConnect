package com.example.agriconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.agriconnect.R;
import com.example.agriconnect.models.PredictionResponse;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder> {

    private List<PredictionResponse.Prediction> predictions = new ArrayList<>();

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prediction_card, parent, false);
        return new PredictionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        PredictionResponse.Prediction prediction = predictions.get(position);
        holder.bind(prediction);
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public void setPredictions(List<PredictionResponse.Prediction> predictions) {
        this.predictions = predictions;
        notifyDataSetChanged();
    }

    static class PredictionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvDay, tvDemand, tvConfidence;

        PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvDemand = itemView.findViewById(R.id.tv_demand);
            tvConfidence = itemView.findViewById(R.id.tv_confidence);
        }

        void bind(PredictionResponse.Prediction prediction) {
            tvDate.setText(prediction.getDate());
            tvDay.setText(prediction.getDayOfWeek());
            tvDemand.setText(String.format("Demand: %.0f units", prediction.getPredictedDemand()));
            tvConfidence.setText(String.format("Range: %.0f - %.0f units",
                    prediction.getConfidenceLower(), prediction.getConfidenceUpper()));
        }
    }
}