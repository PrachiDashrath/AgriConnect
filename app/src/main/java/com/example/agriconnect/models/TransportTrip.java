package com.example.agriconnect;

public class TransportTrip {
    private String tripId;
    private String bookingId;
    private String transporterId;
    private String driverName;
    private String driverPhone;
    private String vehicleNumber;
    private String vehicleType;
    private String currentLocation;
    private double currentLat;
    private double currentLng;
    private String status; // PENDING, PICKED_UP, IN_TRANSIT, NEAR_DESTINATION, DELIVERED
    private String estimatedArrival;
    private String lastUpdated;
    private String startTime;
    private String endTime;
    private String trackingLink;

    public TransportTrip() {}

    public TransportTrip(String tripId, String bookingId, String transporterId) {
        this.tripId = tripId;
        this.bookingId = bookingId;
        this.transporterId = transporterId;
        this.status = "PENDING";
        this.lastUpdated = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                .format(new java.util.Date());
    }

    // Getters and Setters
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getTransporterId() { return transporterId; }
    public void setTransporterId(String transporterId) { this.transporterId = transporterId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double getCurrentLat() { return currentLat; }
    public void setCurrentLat(double currentLat) { this.currentLat = currentLat; }

    public double getCurrentLng() { return currentLng; }
    public void setCurrentLng(double currentLng) { this.currentLng = currentLng; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(String estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getTrackingLink() { return trackingLink; }
    public void setTrackingLink(String trackingLink) { this.trackingLink = trackingLink; }
}