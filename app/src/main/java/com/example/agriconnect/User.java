package com.example.agriconnect;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String email;
    private String name;
    private String phone;
    private String role; // farmer, buyer, inspector
    private String location;
    private String profileImageUrl;
    private String preferredLanguage; // en, hi, mr
    private long createdAt;

    // Empty constructor for Firebase
    public User() {
    }

    public User(String uid, String email, String name, String phone, String role, String location) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.location = location;
        this.profileImageUrl = "";
        this.preferredLanguage = "en";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getLocation() { return location; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
    public void setLocation(String location) { this.location = location; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Convert to Map for Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("email", email);
        map.put("name", name);
        map.put("phone", phone);
        map.put("role", role);
        map.put("location", location);
        map.put("profileImageUrl", profileImageUrl);
        map.put("preferredLanguage", preferredLanguage);
        map.put("createdAt", createdAt);
        return map;
    }
}