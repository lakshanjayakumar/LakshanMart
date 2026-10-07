package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.CartDAO;
import com.lakshan.lakshanmart.dao.OrderDAO;
import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dto.CheckoutRequest;
import com.lakshan.lakshanmart.dto.OrderItemResponseDTO;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.dto.SellerOrderItemDTO;
import com.lakshan.lakshanmart.exception.ForbiddenException;
import com.lakshan.lakshanmart.exception.ResourceNotFoundException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.*;
import com.lakshan.lakshanmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of OrderService managing the order checkout lifecycle.
 */
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderServiceImpl(OrderDAO orderDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public OrderResponseDTO checkout(Long buyerId, CheckoutRequest request) {
        ValidationUtil.requirePositiveId(buyerId, "Buyer");
        if (request == null) {
            throw new ValidationException("Checkout request cannot be null.");
        }
        ValidationUtil.requireNonBlank(request.getShippingAddress(), "Shipping Address");

        List<CartItem> cartItems = cartDAO.findByUserId(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("Your cart is empty. Please add items before checking out.");
        }

        // Verify stock for all items
        BigDecimal orderTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product product = productDAO.findById(ci.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + ci.getProductId() + " no longer exists."));

            if (ci.getQuantity() > product.getStockQty()) {
                throw new ValidationException("Item '" + product.getName() + "' only has " + product.getStockQty() +
                        " units in stock, but your cart has " + ci.getQuantity() + ".");
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            orderTotal = orderTotal.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus(OrderStatus.CONFIRMED); // Auto-confirmed via mock payment step per Section 1 F5
        order.setTotalAmount(orderTotal);
        order.setShippingAddress(request.getShippingAddress().trim());

        Order createdOrder = orderDAO.createOrder(order, orderItems);
        logger.info("Checkout completed successfully: Order ID {}, total {}", createdOrder.getId(), orderTotal);

        return toOrderResponseDTO(createdOrder);
    }

    @Override
    public OrderResponseDTO getOrderDetails(Long requesterId, String requesterRole, Long orderId) {
        ValidationUtil.requirePositiveId(requesterId, "User");
        ValidationUtil.requirePositiveId(orderId, "Order");

        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        boolean isAdmin = Role.ADMIN.name().equalsIgnoreCase(requesterRole);
        boolean isBuyer = order.getBuyerId().equals(requesterId);

        if (!isAdmin && !isBuyer) {
            logger.warn("User {} attempted to view unauthorized order {}", requesterId, orderId);
            throw new ForbiddenException("You are not authorized to view this order.");
        }

        return toOrderResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getUserOrders(Long buyerId) {
        ValidationUtil.requirePositiveId(buyerId, "Buyer");
        List<Order> orders = orderDAO.findByBuyerId(buyerId);
        return orders.stream().map(this::toOrderResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderDAO.findAll();
        return orders.stream().map(this::toOrderResponseDTO).collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, String newStatus) {
        ValidationUtil.requirePositiveId(orderId, "Order");
        ValidationUtil.requireNonBlank(newStatus, "Status");

        OrderStatus targetStatus = OrderStatus.fromString(newStatus);
        if (targetStatus == null) {
            throw new ValidationException("Invalid order status: " + newStatus +
                    ". Allowed statuses: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED.");
        }

        Order existing = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        boolean updated = orderDAO.updateStatus(orderId, targetStatus.name());
        if (!updated) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }

        existing.setStatus(targetStatus);
        logger.info("Order {} status updated to {}", orderId, targetStatus);
        return toOrderResponseDTO(existing);
    }

    @Override
    public List<SellerOrderItemDTO> getSellerOrderItems(Long sellerId) {
        ValidationUtil.requirePositiveId(sellerId, "Seller");
        return orderDAO.findItemsBySellerId(sellerId);
    }

    private OrderResponseDTO toOrderResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem oi : order.getItems()) {
                itemDTOs.add(new OrderItemResponseDTO(
                        oi.getId(),
                        oi.getProductId(),
                        oi.getProductName(),
                        oi.getProductImageUrl(),
                        oi.getQuantity(),
                        oi.getUnitPrice(),
                        oi.getSubtotal()
                ));
            }
        }

        return new OrderResponseDTO(
                order.getId(),
                order.getBuyerId(),
                order.getBuyerName(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                itemDTOs
        );
    }
}
