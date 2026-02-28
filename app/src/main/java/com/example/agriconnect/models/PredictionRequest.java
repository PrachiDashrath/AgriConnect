package com.example.agriconnect.models;

public class PredictionRequest {
    private String vegetable;
    private String district;
    private int days_ahead;
    private String return_format;

    public PredictionRequest(String vegetable, String district, int days_ahead) {
        this.vegetable = vegetable;
        this.district = district;
        this.days_ahead = days_ahead;
        this.return_format = "json";
    }

    // Getters and setters
    public String getVegetable() { return vegetable; }
    public void setVegetable(String vegetable) { this.vegetable = vegetable; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public int getDays_ahead() { return days_ahead; }
    public void setDays_ahead(int days_ahead) { this.days_ahead = days_ahead; }

    public String getReturn_format() { return return_format; }
    public void setReturn_format(String return_format) { this.return_format = return_format; }
}