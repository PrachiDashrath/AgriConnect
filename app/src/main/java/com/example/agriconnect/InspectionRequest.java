package com.example.agriconnect;

public class InspectionRequest {

    private String cropName;
    private String farmerName;
    private String location;
    private int daysValid; // how many days the crop can be sold
    private boolean approved;

    public InspectionRequest() {} // Empty constructor for Firebase

    public InspectionRequest(String cropName, String farmerName, String location, int daysValid, boolean approved) {
        this.cropName = cropName;
        this.farmerName = farmerName;
        this.location = location;
        this.daysValid = daysValid;
        this.approved = approved;
    }

    // Getters and setters
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getDaysValid() { return daysValid; }
    public void setDaysValid(int daysValid) { this.daysValid = daysValid; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
