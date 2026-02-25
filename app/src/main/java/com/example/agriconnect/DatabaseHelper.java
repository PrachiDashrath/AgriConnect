package com.example.agriconnect;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static List<Crop> crops = new ArrayList<>();

    // Add a new crop
    public static void addCrop(String name, String category, double price) {
     //   Crop crop = new Crop(name, category, "FarmerXYZ", price); // Farmer name placeholder
      //  crops.add(crop);
    }

    // Get all crops
    public static List<Crop> getCrops() {
        return crops;
    }

    // Optional: Clear all crops
    public static void clearCrops() {
        crops.clear();
    }
}
