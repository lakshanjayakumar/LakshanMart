package com.lakshan.lakshanmart.dto;

/**
 * Request payload for placing an order from cart contents via mock payment.
 */
public class CheckoutRequest {

    private String shippingAddress;
    private String paymentMethod; // e.g. "CARD", "UPI", "COD"

    public CheckoutRequest() {
    }

    public CheckoutRequest(String shippingAddress, String paymentMethod) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
