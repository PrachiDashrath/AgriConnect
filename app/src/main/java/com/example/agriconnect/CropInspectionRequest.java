package com.example.agriconnect;

public class CropInspectionRequest {

    private String requestId;
    private String farmerName;
    private String cropName;
    private String category;
    private String location;
    private String status; // PENDING / APPROVED / REJECTED

    // Empty constructor (IMPORTANT for Firebase later)
    public CropInspectionRequest() {
    }

    // Main constructor
    public CropInspectionRequest(String requestId,
                                 String farmerName,
                                 String cropName,
                                 String category,
                                 String location,
                                 String status) {
        this.requestId = requestId;
        this.farmerName = farmerName;
        this.cropName = cropName;
        this.category = category;
        this.location = location;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public String getCropName() {
        return cropName;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
