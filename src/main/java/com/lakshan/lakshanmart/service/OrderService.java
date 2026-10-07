package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dto.CheckoutRequest;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;

import java.util.List;

/**
 * Service interface for order checkout and lifecycle tracking.
 */
public interface OrderService {

    /**
     * Executes mock checkout, creating an order from the user's current shopping cart.
     */
    OrderResponseDTO checkout(Long buyerId, CheckoutRequest request);

    /**
     * Retrieves an order's full details, checking user ownership or admin rights.
     */
    OrderResponseDTO getOrderDetails(Long requesterId, String requesterRole, Long orderId);

    /**
     * Lists all orders placed by the specified buyer.
     */
    List<OrderResponseDTO> getUserOrders(Long buyerId);

    /**
     * Lists all orders across the entire marketplace (for Admin review).
     */
    List<OrderResponseDTO> getAllOrders();

    /**
     * Updates an order's fulfillment status (Pending -> Confirmed -> Shipped -> Delivered -> Cancelled).
     */
    OrderResponseDTO updateOrderStatus(Long orderId, String newStatus);

    /**
     * Retrieves all order items placed for products listed by a seller.
     */
    List<com.lakshan.lakshanmart.dto.SellerOrderItemDTO> getSellerOrderItems(Long sellerId);
}
