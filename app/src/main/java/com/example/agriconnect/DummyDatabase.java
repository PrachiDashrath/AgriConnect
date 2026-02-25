package com.example.agriconnect;

import java.util.ArrayList;
import java.util.List;

public class DummyDatabase {

    // ---------------- CROPS ----------------
    private static List<Crop> crops = new ArrayList<>();

    public static void addCrop(Crop crop) {
        crops.add(crop);
    }

    public static List<Crop> getAllCrops() {
        return crops;
    }

    // ---------------- BUYERS ----------------
    private static List<Buyer> buyers = new ArrayList<>();

    public static void addBuyer(Buyer buyer) {
        buyers.add(buyer);
    }

    public static List<Buyer> getAllBuyers() {
        return buyers;
    }

    // ---------------- NOTIFICATIONS ----------------
    private static List<FarmerNotification> notifications = new ArrayList<>();

    public static void addNotification(FarmerNotification notification) {
        notifications.add(notification);
    }

    public static List<FarmerNotification> getNotificationsForFarmer(String farmerName) {
        List<FarmerNotification> result = new ArrayList<>();
        for (FarmerNotification n : notifications) {
            if (n.getFarmerName().equals(farmerName)) {
                result.add(n);
            }
        }
        return result;
    }

    // ---------------- CROP INSPECTION REQUESTS ----------------
    private static List<CropInspectionRequest> inspectionRequests = new ArrayList<>();

    public static void addInspectionRequest(CropInspectionRequest request) {
        inspectionRequests.add(request);
    }

    public static List<CropInspectionRequest> getInspectionRequestsByLocation(String location) {
        List<CropInspectionRequest> result = new ArrayList<>();
        for (CropInspectionRequest r : inspectionRequests) {
            if (r.getLocation().equalsIgnoreCase(location)) {
                result.add(r);
            }
        }
        return result;
    }

    // ---------------- SEND NOTIFICATION TO FARMER ----------------
    public static void notifyFarmer(String farmerName, String message) {
        FarmerNotification notification = new FarmerNotification(farmerName, message);
        addNotification(notification); // save in notifications list
        System.out.println("Notification sent to " + farmerName + ": " + message);
    }
}
