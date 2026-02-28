package com.example.agriconnect.models;

import java.io.Serializable;

public class Transporter implements Serializable {
    private String id;
    private String name;
    private String ownerName;
    private String contact;
    private String location;
    private String vehicleType;
    public String pricePerKm;
    private String gstNumber;
    private String licenseNumber;
    private String insuranceNumber;
    private double rating;
    private int totalTrips;
    private boolean isVerified;

    // Required empty constructor for Firebase
    public Transporter() {}

    // Constructor for registration
    public Transporter(String name, String vehicleType, String contact,
                       String location, String pricePerKm) {
        this.name = name;
        this.vehicleType = vehicleType;
        this.contact = contact;
        this.location = location;
        this.pricePerKm = pricePerKm;
        this.rating = 0.0;
        this.totalTrips = 0;
        this.isVerified = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getPricePerKm() { return pricePerKm; }
    public void setPricePerKm(String pricePerKm) { this.pricePerKm = pricePerKm; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalTrips() { return totalTrips; }
    public void setTotalTrips(int totalTrips) { this.totalTrips = totalTrips; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}