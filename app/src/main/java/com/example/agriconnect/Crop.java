package com.example.agriconnect;

public class Crop {
    private String cropId, farmerId, farmerName, farmerPhone, cropName, category, status, inspectorFeedback, price, quantity, location;
    private long timestamp;

    public Crop() {} // Required for Firebase

    public Crop(String cropId, String farmerId, String farmerName, String farmerPhone, String cropName, String category,
                String price, String quantity, String status, String inspectorFeedback, String location, long timestamp) {
        this.cropId = cropId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.cropName = cropName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.inspectorFeedback = inspectorFeedback;
        this.location = location;
        this.timestamp = timestamp;
    }

    // Getters
    public String getCropId() { return cropId; }
    public String getFarmerId() { return farmerId; }
    public String getFarmerName() { return farmerName; }
    public String getFarmerPhone() { return farmerPhone; }
    public String getCropName() { return cropName; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public String getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getInspectorFeedback() { return inspectorFeedback; }
    public String getLocation() { return location; }
    public long getTimestamp() { return timestamp; }
}