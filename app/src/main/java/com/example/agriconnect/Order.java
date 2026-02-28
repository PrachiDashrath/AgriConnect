package com.example.agriconnect;

import java.io.Serializable;

public class Order implements Serializable {
    public String cropName, buyerLocation, quantity, totalBill, farmerPhone;

    public Order(String cropName, String buyerLocation, String quantity, String totalBill, String farmerPhone) {
        this.cropName = cropName;
        this.buyerLocation = buyerLocation;
        this.quantity = quantity;
        this.totalBill = totalBill;
        this.farmerPhone = farmerPhone;
    }
}