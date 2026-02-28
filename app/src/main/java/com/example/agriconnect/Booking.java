package com.example.agriconnect;

import java.io.Serializable;

public class Booking implements Serializable {
    public String bookingId, cropName, totalAmount, buyerLocation, status, transporterName, farmerPhone;

    public Booking() {} // Required for Firebase

    public Booking(String id, String crop, String amount, String loc, String stat, String trans, String phone) {
        this.bookingId = id;
        this.cropName = crop;
        this.totalAmount = amount;
        this.buyerLocation = loc;
        this.status = stat;
        this.transporterName = trans;
        this.farmerPhone = phone;
    }
}