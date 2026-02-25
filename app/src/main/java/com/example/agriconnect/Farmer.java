package com.example.agriconnect;

public class Farmer {
    private String name;
    private String contact;
    private String city;
    private String userType;
    private boolean approved;

    public Farmer() {}

    public Farmer(String name, String contact, String city, String userType, boolean approved) {
        this.name = name;
        this.contact = contact;
        this.city = city;
        this.userType = userType;
        this.approved = approved;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getCity() { return city; }
    public String getUserType() { return userType; }
    public boolean isApproved() { return approved; }
}