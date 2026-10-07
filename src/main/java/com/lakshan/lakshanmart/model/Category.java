package com.lakshan.lakshanmart.model;

import java.sql.Timestamp;

/**
 * Domain entity representing a product category in the marketplace.
 */
public class Category {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private Timestamp createdAt;

    public Category() {
    }

    public Category(Long id, String name, String description, String icon, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
