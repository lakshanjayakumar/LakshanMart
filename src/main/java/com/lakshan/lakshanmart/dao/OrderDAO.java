package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.Order;
import com.lakshan.lakshanmart.model.OrderItem;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Order placement and lifecycle tracking.
 */
public interface OrderDAO {

    /**
     * Atomically creates an Order and its line items, decrements product stock,
     * and clears the user's cart in a single transaction.
     */
    Order createOrder(Order order, List<OrderItem> items);

    /**
     * Finds an order by its primary key ID including line items.
     */
    Optional<Order> findById(Long orderId);

    /**
     * Finds all orders placed by a specific buyer.
     */
    List<Order> findByBuyerId(Long buyerId);

    /**
     * Finds all orders across the marketplace (for Admin order management).
     */
    List<Order> findAll();

    /**
     * Updates an order's lifecycle status (Pending -> Confirmed -> Shipped -> Delivered -> Cancelled).
     */
    boolean updateStatus(Long orderId, String status);

    /**
     * Checks if a buyer has purchased and received a product, required for review validation.
     */
    boolean hasPurchasedProduct(Long buyerId, Long productId);

    /**
     * Finds all order items placed for products owned by a specific seller.
     */
    List<com.lakshan.lakshanmart.dto.SellerOrderItemDTO> findItemsBySellerId(Long sellerId);
}
