package com.prm392.salesapp;

public class CreateOrderRequest {
    private String paymentMethod;
    private String billingAddress;

    public CreateOrderRequest(String paymentMethod, String billingAddress) {
        this.paymentMethod = paymentMethod;
        this.billingAddress = billingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getBillingAddress() {
        return billingAddress;
    }
}
