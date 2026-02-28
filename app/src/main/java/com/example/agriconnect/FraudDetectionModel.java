package com.example.agriconnect;

public class FraudDetectionModel {

    public static double calculateFinalRating(int totalStars, int totalRatings, int reportCount) {

        if (totalRatings == 0) return 0;

        double averageRating = (double) totalStars / totalRatings;
        double penalty = 0.2 * reportCount;

        double finalRating = averageRating - penalty;

        if (finalRating < 0) finalRating = 0;
        if (finalRating > 5) finalRating = 5;

        return Math.round(finalRating * 100.0) / 100.0;
    }
}