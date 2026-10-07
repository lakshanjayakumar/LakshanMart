package com.lakshan.lakshanmart.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing the complete state of a user's shopping cart.
 */
public class CartResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CartItemDTO> items = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private int itemCount = 0;

    public CartResponseDTO() {
    }

    public CartResponseDTO(List<CartItemDTO> items, BigDecimal totalAmount, int itemCount) {
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        this.itemCount = itemCount;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
