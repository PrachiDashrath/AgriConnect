package com.example.agriconnect.utils;

public class TransportCalculator {

    private static final double GST_PERCENTAGE = 18.0;
    private static final double TOLL_PER_100KM = 150.0;
    private static final double LOADING_CHARGE = 200.0;
    private static final double UNLOADING_CHARGE = 200.0;
    private static final double INSURANCE_PERCENTAGE = 0.5;

    public static class CostBreakdown {
        public double baseFare;
        public double tollCharges;
        public double loadingCharges;
        public double unloadingCharges;
        public double insuranceCharges;
        public double subtotal;
        public double gstAmount;
        public double totalAmount;

        public String getBaseFareFormatted() { return "₹" + String.format("%.2f", baseFare); }
        public String getTollFormatted() { return "₹" + String.format("%.2f", tollCharges); }
        public String getLoadingFormatted() { return "₹" + String.format("%.2f", loadingCharges); }
        public String getUnloadingFormatted() { return "₹" + String.format("%.2f", unloadingCharges); }
        public String getInsuranceFormatted() { return "₹" + String.format("%.2f", insuranceCharges); }
        public String getSubtotalFormatted() { return "₹" + String.format("%.2f", subtotal); }
        public String getGstFormatted() { return "₹" + String.format("%.2f", gstAmount); }
        public String getTotalFormatted() { return "₹" + String.format("%.2f", totalAmount); }
    }

    public static CostBreakdown calculateTotalCost(double distanceKm, double pricePerKm,
                                                   boolean needInsurance) {
        CostBreakdown cost = new CostBreakdown();

        cost.baseFare = distanceKm * pricePerKm;
        cost.tollCharges = (distanceKm / 100) * TOLL_PER_100KM;
        cost.loadingCharges = LOADING_CHARGE;
        cost.unloadingCharges = UNLOADING_CHARGE;
        cost.insuranceCharges = needInsurance ? (cost.baseFare * INSURANCE_PERCENTAGE / 100) : 0;
        cost.subtotal = cost.baseFare + cost.tollCharges + cost.loadingCharges +
                cost.unloadingCharges + cost.insuranceCharges;
        cost.gstAmount = cost.subtotal * (GST_PERCENTAGE / 100);
        cost.totalAmount = cost.subtotal + cost.gstAmount;

        return cost;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public static String getEstimatedDeliveryTime(double distanceKm) {
        double hours = distanceKm / 40;
        int totalMinutes = (int)(hours * 60);

        if (totalMinutes < 60) {
            return totalMinutes + " minutes";
        } else {
            int hrs = totalMinutes / 60;
            int mins = totalMinutes % 60;
            return hrs + " hours " + mins + " minutes";
        }
    }
}