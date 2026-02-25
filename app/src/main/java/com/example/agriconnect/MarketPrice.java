package com.example.agriconnect;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MarketPrice {
    // Variable names must match Firebase keys exactly
    public String cropName, price, location, category, quantity, grade, inspectorFeedback, farmerName, farmerPhone;

    // Required empty constructor for Firebase
    public MarketPrice() {}

    // Full constructor for the Inspector to use
    public MarketPrice(String cropName, String price, String location, String category,
                       String quantity, String grade, String inspectorFeedback,
                       String farmerName, String farmerPhone) {
        this.cropName = cropName;
        this.price = price;
        this.location = location;
        this.category = category;
        this.quantity = quantity;
        this.grade = grade;
        this.inspectorFeedback = inspectorFeedback;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
    }

    // Getters for the Adapter to use
    public String getCropName() { return cropName; }
    public String getPrice() { return price; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public String getQuantity() { return quantity; }
    public String getGrade() { return grade; }
    public String getInspectorFeedback() { return inspectorFeedback; }
    public String getFarmerName() { return farmerName; }
    public String getFarmerPhone() { return farmerPhone; }
}