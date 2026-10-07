package com.lakshan.lakshanmart.service;

import java.util.Locale;

/**
 * Intelligent domain-restricted MockChatProvider providing canned FAQ answers
 * for LakshanMart e-commerce queries (zero external network dependency).
 */
public class MockChatProvider implements ChatProvider {

    @Override
    public String getReply(String userMessage, String context) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! I am your LakshanMart AI Assistant. How can I help with your shopping today?";
        }

        String msg = userMessage.trim().toLowerCase(Locale.ROOT);

        if (msg.contains("order") && (msg.contains("track") || msg.contains("status") || msg.contains("where"))) {
            return "You can view and track your orders anytime by clicking on 'My Orders' in the top navigation bar. Each order displays its current status (Pending, Confirmed, Shipped, or Delivered).";
        }

        if (msg.contains("return") || msg.contains("refund")) {
            return "LakshanMart offers a hassle-free 7-day return policy on all eligible items. Ensure the product remains in its original packaging with all tags intact.";
        }

        if (msg.contains("shipping") || msg.contains("delivery") || msg.contains("cost")) {
            return "Standard delivery takes 2–4 business days across India. We offer free shipping on all orders over ₹499 ($25)!";
        }

        if (msg.contains("payment") || msg.contains("pay") || msg.contains("upi") || msg.contains("card")) {
            return "We accept Credit/Debit Cards, UPI, Net Banking, and Cash on Delivery (COD) via our simulated secure checkout.";
        }

        if (msg.contains("sell") || msg.contains("seller") || msg.contains("merchant") || msg.contains("vendor")) {
            return "Interested in selling on LakshanMart? Simply select the 'Seller' role when registering an account to instantly list and manage your products from the Seller Dashboard!";
        }

        if (msg.contains("category") || msg.contains("categories") || msg.contains("what do you sell")) {
            return "LakshanMart features items across 5 core categories: Electronics, Fashion, Home & Kitchen, Books & Stationery, and Sports & Fitness. Explore the top navigation menu to browse!";
        }

        if (msg.contains("discount") || msg.contains("coupon") || msg.contains("offer") || msg.contains("deal")) {
            return "Check out our homepage banner for today's special seasonal offers! Many top electronics and apparel items are currently discounted up to 30%.";
        }

        if (msg.contains("review") || msg.contains("rating") || msg.contains("star")) {
            return "Verified buyers who have ordered an item can submit a 1 to 5-star rating and written review on any completed order.";
        }

        if (msg.contains("hello") || msg.contains("hi") || msg.contains("hey")) {
            return "Hello! Welcome to LakshanMart. How can I assist your shopping journey today?";
        }

        if (msg.contains("contact") || msg.contains("support") || msg.contains("help")) {
            return "Our customer support team is available 24/7 at support@lakshanmart.com. You can also ask me any questions about our products and order process!";
        }

        // Domain-scoped fallback
        return "I am the LakshanMart assistant. I can assist with product recommendations, order tracking, shipping, return policies, or seller registration. How may I help you?";
    }
}
