package com.lakshan.lakshanmart.model;

/**
 * User roles within LakshanMart marketplace.
 * <p>BUYER and SELLER are available through registration; ADMIN is provisioned via seed data.</p>
 */
public enum Role {
    BUYER,
    SELLER,
    ADMIN;

    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            return null;
        }
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(roleStr.trim())) {
                return r;
            }
        }
        return null;
    }
}
