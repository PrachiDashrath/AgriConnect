package com.example.agriconnect.models;

import java.util.List;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

public class PredictionResponse {
    private boolean success;
    private String vegetable;
    private String district;

    @SerializedName("days_ahead")
    private int daysAhead;

    private List<Prediction> predictions;
    private Map<String, Object> summary;
    private String unit;
    private String currency;
    private String error;

    public static class Prediction {
        private String date;

        @SerializedName("day_of_week")
        private String dayOfWeek;

        @SerializedName("predicted_demand")
        private double predictedDemand;

        @SerializedName("predicted_price")
        private double predictedPrice;

        @SerializedName("confidence_lower")
        private double confidenceLower;

        @SerializedName("confidence_upper")
        private double confidenceUpper;

        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

        public double getPredictedDemand() { return predictedDemand; }
        public void setPredictedDemand(double predictedDemand) {
            this.predictedDemand = predictedDemand;
        }

        public double getPredictedPrice() { return predictedPrice; }
        public void setPredictedPrice(double predictedPrice) {
            this.predictedPrice = predictedPrice;
        }

        public double getConfidenceLower() { return confidenceLower; }
        public void setConfidenceLower(double confidenceLower) {
            this.confidenceLower = confidenceLower;
        }

        public double getConfidenceUpper() { return confidenceUpper; }
        public void setConfidenceUpper(double confidenceUpper) {
            this.confidenceUpper = confidenceUpper;
        }
    }

    // Getters and setters for main response
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getVegetable() { return vegetable; }
    public void setVegetable(String vegetable) { this.vegetable = vegetable; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public int getDaysAhead() { return daysAhead; }
    public void setDaysAhead(int daysAhead) { this.daysAhead = daysAhead; }

    public List<Prediction> getPredictions() { return predictions; }
    public void setPredictions(List<Prediction> predictions) { this.predictions = predictions; }

    public Map<String, Object> getSummary() { return summary; }
    public void setSummary(Map<String, Object> summary) { this.summary = summary; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}