package com.example.agriconnect.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class VegetableResponse {
    private boolean success;
    private List<Vegetable> vegetables;
    private int total;
    private String error;

    public static class Vegetable {
        private String name;

        @SerializedName("demand_models")
        private int demandModels;

        @SerializedName("price_models")
        private int priceModels;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getDemandModels() { return demandModels; }
        public void setDemandModels(int demandModels) { this.demandModels = demandModels; }

        public int getPriceModels() { return priceModels; }
        public void setPriceModels(int priceModels) { this.priceModels = priceModels; }
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<Vegetable> getVegetables() { return vegetables; }
    public void setVegetables(List<Vegetable> vegetables) { this.vegetables = vegetables; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}