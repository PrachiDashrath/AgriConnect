package com.example.agriconnect;

public class FarmerNotification {

    private String farmerName;
    private String message;

    public FarmerNotification(String farmerName, String message) {
        this.farmerName = farmerName;
        this.message = message;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public String getMessage() {
        return message;
    }
}
