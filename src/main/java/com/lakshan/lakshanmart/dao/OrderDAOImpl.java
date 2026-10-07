package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.dto.SellerOrderItemDTO;
import com.lakshan.lakshanmart.exception.DatabaseException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.Order;
import com.lakshan.lakshanmart.model.OrderItem;
import com.lakshan.lakshanmart.model.OrderStatus;
import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of OrderDAO with transaction control and PreparedStatements.
 */
public class OrderDAOImpl implements OrderDAO {

    private static final Logger logger = LoggerFactory.getLogger(OrderDAOImpl.class);

    private static final String INSERT_ORDER_SQL =
            "INSERT INTO orders (buyer_id, status, total_amount, shipping_address, created_at) VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_ORDER_ITEM_SQL =
            "INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at) VALUES (?, ?, ?, ?, ?)";

    private static final String DEDUCT_STOCK_SQL =
            "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";

    private static final String CLEAR_USER_CART_SQL =
            "DELETE FROM cart_items WHERE user_id = ?";

    private static final String FIND_ORDER_BY_ID_SQL =
            "SELECT o.id, o.buyer_id, o.status, o.total_amount, o.shipping_address, o.created_at, " +
            "u.name AS buyer_name, u.email AS buyer_email " +
            "FROM orders o " +
            "JOIN users u ON o.buyer_id = u.id " +
            "WHERE o.id = ?";

    private static final String FIND_ORDER_ITEMS_SQL =
            "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, oi.created_at, " +
            "p.name AS product_name, p.image_url AS product_image_url " +
            "FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE oi.order_id = ? " +
            "ORDER BY oi.id ASC";

    private static final String FIND_ORDERS_BY_BUYER_SQL =
            "SELECT o.id, o.buyer_id, o.status, o.total_amount, o.shipping_address, o.created_at, " +
            "u.name AS buyer_name, u.email AS buyer_email " +
            "FROM orders o " +
            "JOIN users u ON o.buyer_id = u.id " +
            "WHERE o.buyer_id = ? " +
            "ORDER BY o.id DESC";

    private static final String FIND_ALL_ORDERS_SQL =
            "SELECT o.id, o.buyer_id, o.status, o.total_amount, o.shipping_address, o.created_at, " +
            "u.name AS buyer_name, u.email AS buyer_email " +
            "FROM orders o " +
            "JOIN users u ON o.buyer_id = u.id " +
            "ORDER BY o.id DESC";

    private static final String UPDATE_ORDER_STATUS_SQL =
            "UPDATE orders SET status = ? WHERE id = ?";

    private static final String HAS_PURCHASED_PRODUCT_SQL =
            "SELECT 1 FROM orders o " +
            "JOIN order_items oi ON o.id = oi.order_id " +
            "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status IN ('DELIVERED', 'CONFIRMED', 'SHIPPED') " +
            "LIMIT 1";

    private static final String FIND_SELLER_ORDER_ITEMS_SQL =
            "SELECT oi.order_id, o.created_at, o.status, u.name AS buyer_name, " +
            "oi.product_id, oi.product_name, oi.product_image_url, " +
            "oi.quantity, oi.unit_price, (oi.quantity * oi.unit_price) AS subtotal " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN users u ON o.buyer_id = u.id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE p.seller_id = ? " +
            "ORDER BY o.id DESC";

    @Override
    public Order createOrder(Order order, List<OrderItem> items) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            Timestamp now = (order.getCreatedAt() != null) ? order.getCreatedAt() : new Timestamp(System.currentTimeMillis());

            // 1. Insert Order
            Long orderId;
            try (PreparedStatement psOrder = conn.prepareStatement(INSERT_ORDER_SQL, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setLong(1, order.getBuyerId());
                psOrder.setString(2, order.getStatus().name());
                psOrder.setBigDecimal(3, order.getTotalAmount());
                psOrder.setString(4, order.getShippingAddress());
                psOrder.setTimestamp(5, now);

                psOrder.executeUpdate();
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getLong(1);
                        order.setId(orderId);
                        order.setCreatedAt(now);
                    } else {
                        throw new DatabaseException("Failed to generate order ID");
                    }
                }
            }

            // 2. Insert Order Items & Deduct Stock
            try (PreparedStatement psItem = conn.prepareStatement(INSERT_ORDER_ITEM_SQL, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psDeduct = conn.prepareStatement(DEDUCT_STOCK_SQL)) {

                for (OrderItem item : items) {
                    // Decrement stock
                    psDeduct.setInt(1, item.getQuantity());
                    psDeduct.setLong(2, item.getProductId());
                    psDeduct.setInt(3, item.getQuantity());
                    int updatedStock = psDeduct.executeUpdate();
                    if (updatedStock == 0) {
                        throw new ValidationException("Product ID " + item.getProductId() + " does not have sufficient stock.");
                    }

                    // Insert order item
                    psItem.setLong(1, orderId);
                    psItem.setLong(2, item.getProductId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setBigDecimal(4, item.getUnitPrice());
                    psItem.setTimestamp(5, now);
                    psItem.executeUpdate();

                    try (ResultSet rsItem = psItem.getGeneratedKeys()) {
                        if (rsItem.next()) {
                            item.setId(rsItem.getLong(1));
                        }
                    }
                    item.setOrderId(orderId);
                    item.setCreatedAt(now);
                }
            }

            // 3. Clear Cart for Buyer
            try (PreparedStatement psClear = conn.prepareStatement(CLEAR_USER_CART_SQL)) {
                psClear.setLong(1, order.getBuyerId());
                psClear.executeUpdate();
            }

            conn.commit(); // Commit transaction
            order.setItems(items);
            logger.info("Order {} created successfully for buyer {}", orderId, order.getBuyerId());
            return order;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Failed to rollback order transaction: {}", ex.getMessage());
                }
            }
            if (e instanceof ValidationException) {
                throw (ValidationException) e;
            }
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to place order: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error resetting connection auto-commit: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ORDER_BY_ID_SQL)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    order.setItems(findItemsByOrderId(orderId, conn));
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding order by id {}: {}", orderId, e.getMessage(), e);
            throw new DatabaseException("Failed to query order", e);
        }
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ORDERS_BY_BUYER_SQL)) {

            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    order.setItems(findItemsByOrderId(order.getId(), conn));
                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            logger.error("Error finding orders for buyer {}: {}", buyerId, e.getMessage(), e);
            throw new DatabaseException("Failed to list buyer orders", e);
        }
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_ORDERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = mapRowToOrder(rs);
                order.setItems(findItemsByOrderId(order.getId(), conn));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            logger.error("Error retrieving all orders: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve orders", e);
        }
    }

    @Override
    public boolean updateStatus(Long orderId, String status) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ORDER_STATUS_SQL)) {

            ps.setString(1, status.trim().toUpperCase());
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating order {} status to {}: {}", orderId, status, e.getMessage(), e);
            throw new DatabaseException("Failed to update order status", e);
        }
    }

    @Override
    public boolean hasPurchasedProduct(Long buyerId, Long productId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(HAS_PURCHASED_PRODUCT_SQL)) {

            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking purchased status for buyer {} product {}: {}", buyerId, productId, e.getMessage(), e);
            throw new DatabaseException("Failed to verify product purchase history", e);
        }
    }

    @Override
    public List<SellerOrderItemDTO> findItemsBySellerId(Long sellerId) {
        List<SellerOrderItemDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_SELLER_ORDER_ITEMS_SQL)) {

            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellerOrderItemDTO item = new SellerOrderItemDTO(
                            rs.getLong("order_id"),
                            rs.getTimestamp("created_at"),
                            rs.getString("status"),
                            rs.getString("buyer_name"),
                            rs.getLong("product_id"),
                            rs.getString("product_name"),
                            rs.getString("product_image_url"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price"),
                            rs.getBigDecimal("subtotal")
                    );
                    list.add(item);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding order items for seller {}: {}", sellerId, e.getMessage(), e);
            throw new DatabaseException("Failed to list seller orders", e);
        }
    }

    private List<OrderItem> findItemsByOrderId(Long orderId, Connection conn) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(FIND_ORDER_ITEMS_SQL)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image_url"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setBuyerId(rs.getLong("buyer_id"));
        order.setStatus(OrderStatus.fromString(rs.getString("status")));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setBuyerName(rs.getString("buyer_name"));
        order.setBuyerEmail(rs.getString("buyer_email"));
        return order;
    }
}
