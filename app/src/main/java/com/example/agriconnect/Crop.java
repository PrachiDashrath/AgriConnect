
package com.example.agriconnect;

public class Crop {

    private String cropId;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;

    private String cropName;
    private String category;
    private String price;
    private String quantity;
    private String status;
    private String inspectorFeedback;
    private String location;

    // ✅ ADD THESE
    private String village;
    private String state;

    private long timestamp;

    public Crop() {
        // Required for Firebase
    }

    public Crop(String cropId,
                String farmerId,
                String farmerName,
                String farmerPhone,
                String cropName,
                String category,
                String price,
                String quantity,
                String status,
                String inspectorFeedback,
                String location,
                String village,
                String state,
                long timestamp) {

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
        this.village = village;
        this.state = state;
        this.timestamp = timestamp;
    }

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

}