package com.example.agriconnect;

public class Buyer {
    private String buyerId, name, contact, city;

    // Required for Firebase
    public Buyer() {}

    public Buyer(String buyerId, String name, String contact, String city) {
        this.buyerId = buyerId;
        this.name = name;
        this.contact = contact;
        this.city = city;
    }

    public String getBuyerId() { return buyerId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getCity() { return city; }
}