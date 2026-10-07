package com.lakshan.lakshanmart.model;

/**
 * Lifecycle status of an Order per R2025 spec:
 * PENDING -> CONFIRMED -> SHIPPED -> DELIVERED (or CANCELLED)
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public static OrderStatus fromString(String statusStr) {
        if (statusStr == null) {
            return null;
        }
        for (OrderStatus s : OrderStatus.values()) {
            if (s.name().equalsIgnoreCase(statusStr.trim())) {
                return s;
            }
        }
        return null;
    }
}
