package com.example.agriconnect.models;

public class TransportCompany {
    private String companyId;
    private String companyName;
    private String ownerName;
    private String phoneNumber;
    private String email;
    private String address;
    private String gstNumber;
    private String vehicleTypes; // Truck, Mini Truck, Tempo, etc.
    private double pricePerKm;
    private double rating;
    private int totalTrips;
    private String licenseNumber;
    private String insuranceNumber;
    private boolean isVerified;
    private String logoUrl;

    // Empty constructor for Firebase
    public TransportCompany() {}

    public TransportCompany(String companyId, String companyName, String ownerName,
                            String phoneNumber, String email, String address,
                            String gstNumber, String vehicleTypes, double pricePerKm) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.gstNumber = gstNumber;
        this.vehicleTypes = vehicleTypes;
        this.pricePerKm = pricePerKm;
        this.rating = 0.0;
        this.totalTrips = 0;
        this.isVerified = false;
    }

    // Getters and Setters
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getVehicleTypes() { return vehicleTypes; }
    public void setVehicleTypes(String vehicleTypes) { this.vehicleTypes = vehicleTypes; }

    public double getPricePerKm() { return pricePerKm; }
    public void setPricePerKm(double pricePerKm) { this.pricePerKm = pricePerKm; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalTrips() { return totalTrips; }
    public void setTotalTrips(int totalTrips) { this.totalTrips = totalTrips; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}