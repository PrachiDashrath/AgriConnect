package com.example.agriconnect.models;

public class TransportBill {
    private String billId;
    private String bookingId;
    private String transporterId;
    private String transporterName;
    private String transporterGst;
    private String customerName;
    private String customerPhone;
    private String billDate;
    private String pickupAddress;
    private String destinationAddress;
    private double distanceKm;
    private double baseFare;
    private double pricePerKm;
    private double gstPercentage;
    private double gstAmount;
    private double tollCharges;
    private double loadingCharges;
    private double unloadingCharges;
    private double insuranceCharges;
    private double discount;
    private double totalAmount;
    private String paymentMethod;
    private String transactionId;
    private String billStatus;

    public TransportBill() {}

    public TransportBill(String billId, String bookingId, String transporterId,
                         String transporterName, String transporterGst) {
        this.billId = billId;
        this.bookingId = bookingId;
        this.transporterId = transporterId;
        this.transporterName = transporterName;
        this.transporterGst = transporterGst;
        this.billDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                .format(new java.util.Date());
        this.gstPercentage = 18.0;
        this.loadingCharges = 200.0;
        this.unloadingCharges = 200.0;
        this.billStatus = "GENERATED";
    }

    // Calculate total with all charges
    public void calculateTotal() {
        double subtotal = baseFare + loadingCharges + unloadingCharges +
                insuranceCharges + tollCharges;
        this.gstAmount = subtotal * (gstPercentage / 100);
        this.totalAmount = subtotal + gstAmount - discount;
    }

    // Getters and Setters
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getTransporterId() { return transporterId; }
    public void setTransporterId(String transporterId) { this.transporterId = transporterId; }

    public String getTransporterName() { return transporterName; }
    public void setTransporterName(String transporterName) {
        this.transporterName = transporterName;
    }

    public String getTransporterGst() { return transporterGst; }
    public void setTransporterGst(String transporterGst) {
        this.transporterGst = transporterGst;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public double getBaseFare() { return baseFare; }
    public void setBaseFare(double baseFare) { this.baseFare = baseFare; }

    public double getPricePerKm() { return pricePerKm; }
    public void setPricePerKm(double pricePerKm) { this.pricePerKm = pricePerKm; }

    public double getGstPercentage() { return gstPercentage; }
    public void setGstPercentage(double gstPercentage) { this.gstPercentage = gstPercentage; }

    public double getGstAmount() { return gstAmount; }
    public void setGstAmount(double gstAmount) { this.gstAmount = gstAmount; }

    public double getTollCharges() { return tollCharges; }
    public void setTollCharges(double tollCharges) { this.tollCharges = tollCharges; }

    public double getLoadingCharges() { return loadingCharges; }
    public void setLoadingCharges(double loadingCharges) {
        this.loadingCharges = loadingCharges;
    }

    public double getUnloadingCharges() { return unloadingCharges; }
    public void setUnloadingCharges(double unloadingCharges) {
        this.unloadingCharges = unloadingCharges;
    }

    public double getInsuranceCharges() { return insuranceCharges; }
    public void setInsuranceCharges(double insuranceCharges) {
        this.insuranceCharges = insuranceCharges;
    }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getBillStatus() { return billStatus; }
    public void setBillStatus(String billStatus) { this.billStatus = billStatus; }
}