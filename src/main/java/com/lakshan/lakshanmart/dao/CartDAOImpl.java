package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.exception.DatabaseException;
import com.lakshan.lakshanmart.model.CartItem;
import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CartDAO using PreparedStatements and try-with-resources.
 */
public class CartDAOImpl implements CartDAO {

    private static final Logger logger = LoggerFactory.getLogger(CartDAOImpl.class);

    private static final String SELECT_JOIN_BASE =
            "SELECT c.id, c.user_id, c.product_id, c.quantity, c.created_at, " +
            "p.name AS product_name, p.price AS product_price, p.image_url AS product_image_url, p.stock_qty AS product_stock " +
            "FROM cart_items c " +
            "JOIN products p ON c.product_id = p.id ";

    private static final String FIND_BY_USER_ID_SQL =
            SELECT_JOIN_BASE + "WHERE c.user_id = ? ORDER BY c.id ASC";

    private static final String FIND_BY_USER_AND_PRODUCT_SQL =
            SELECT_JOIN_BASE + "WHERE c.user_id = ? AND c.product_id = ?";

    private static final String INSERT_CART_ITEM_SQL =
            "INSERT INTO cart_items (user_id, product_id, quantity, created_at) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUANTITY_SQL =
            "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";

    private static final String DELETE_ITEM_SQL =
            "DELETE FROM cart_items WHERE id = ? AND user_id = ?";

    private static final String CLEAR_CART_SQL =
            "DELETE FROM cart_items WHERE user_id = ?";

    @Override
    public List<CartItem> findByUserId(Long userId) {
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_ID_SQL)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToCartItem(rs));
                }
            }
            return items;
        } catch (SQLException e) {
            logger.error("Error fetching cart items for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Failed to fetch cart items", e);
        }
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_AND_PRODUCT_SQL)) {

            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCartItem(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding cart item for user {} and product {}: {}", userId, productId, e.getMessage(), e);
            throw new DatabaseException("Failed to query cart item", e);
        }
    }

    @Override
    public CartItem create(CartItem item) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_CART_ITEM_SQL, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = (item.getCreatedAt() != null) ? item.getCreatedAt() : new Timestamp(System.currentTimeMillis());
            ps.setLong(1, item.getUserId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setTimestamp(4, now);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating cart item failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                    item.setCreatedAt(now);
                }
            }
            return item;
        } catch (SQLException e) {
            logger.error("Error creating cart item: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to add item to cart", e);
        }
    }

    @Override
    public boolean updateQuantity(Long cartItemId, Long userId, int quantity) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_QUANTITY_SQL)) {

            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            ps.setLong(3, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating cart item {} quantity: {}", cartItemId, e.getMessage(), e);
            throw new DatabaseException("Failed to update cart item quantity", e);
        }
    }

    @Override
    public boolean delete(Long cartItemId, Long userId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ITEM_SQL)) {

            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting cart item {}: {}", cartItemId, e.getMessage(), e);
            throw new DatabaseException("Failed to delete cart item", e);
        }
    }

    @Override
    public void clearCart(Long userId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(CLEAR_CART_SQL)) {

            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error clearing cart for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Failed to clear shopping cart", e);
        }
    }

    private CartItem mapRowToCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        item.setUserId(rs.getLong("user_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setProductName(rs.getString("product_name"));
        item.setProductPrice(rs.getBigDecimal("product_price"));
        item.setProductImageUrl(rs.getString("product_image_url"));
        item.setProductStock(rs.getInt("product_stock"));
        return item;
    }
}
