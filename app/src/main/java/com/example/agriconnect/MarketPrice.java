package com.example.agriconnect;

import java.io.Serializable;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MarketPrice implements Serializable {

    public String cropName;
    public String price;
    public String quantity;
    public String category;
    public String grade;
    public String inspectorFeedback;
    public String name;       // This represents the Farmer's Name
    public String location;

    public MarketPrice() {
        // Required for Firebase
    }

    // ✅ FIXED CONSTRUCTOR: Matches the order used in InspectorAdapter
    public MarketPrice(String cropName,
                       String price,
                       String location,
                       String category,
                       String quantity,
                       String grade,
                       String inspectorFeedback,
                       String name) {

        this.cropName = cropName;
        this.price = price;
        this.location = location;
        this.category = category;
        this.quantity = quantity;
        this.grade = grade;
        this.inspectorFeedback = inspectorFeedback;
        this.name = name;
    }

    // Getters for RecyclerView and Adapters
    public String getCropName() { return cropName; }
    public String getPrice() { return price; }
    public String getQuantity() { return quantity; }
    public String getCategory() { return category; }
    public String getGrade() { return grade; }
    public String getInspectorFeedback() { return inspectorFeedback; }
    public String getName() { return name; }
    public String getLocation() { return location; }
}