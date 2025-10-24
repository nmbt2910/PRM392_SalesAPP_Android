package com.prm392.salesapp;

public class OrderDetail {
    private int OrderID;
    private int CartID;
    private String PaymentMethod;
    private String BillingAddress;
    private String OrderStatus;
    private String OrderDate;
    private double PaymentAmount;
    private String PaymentStatus;

    public int getOrderID() { return OrderID; }
    public int getCartID() { return CartID; }
    public String getPaymentMethod() { return PaymentMethod; }
    public String getBillingAddress() { return BillingAddress; }
    public String getOrderStatus() { return OrderStatus; }
    public String getOrderDate() { return OrderDate; }
    public double getPaymentAmount() { return PaymentAmount; }
    public String getPaymentStatus() { return PaymentStatus; }
}
