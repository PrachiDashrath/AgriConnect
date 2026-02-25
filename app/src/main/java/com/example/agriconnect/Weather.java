package com.example.agriconnect;

public class Weather {
    private String day, condition, temperature;

    public Weather(String day, String condition, String temperature) {
        this.day = day;
        this.condition = condition;
        this.temperature = temperature;
    }

    public String getDay() { return day; }
    public String getCondition() { return condition; }
    public String getTemperature() { return temperature; }
}
