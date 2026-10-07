package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.exception.DatabaseException;
import com.lakshan.lakshanmart.model.Product;
import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ProductDAO.
 * Strictly uses PreparedStatement and try-with-resources.
 */
public class ProductDAOImpl implements ProductDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProductDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT id, seller_id, name, description, price, stock_qty, category, image_url, created_at FROM products";

    private static final String FIND_BY_ID_SQL =
            SELECT_BASE + " WHERE id = ?";

    private static final String FIND_ALL_SQL =
            SELECT_BASE + " ORDER BY id DESC LIMIT ? OFFSET ?";

    private static final String FIND_BY_CATEGORY_SQL =
            SELECT_BASE + " WHERE category = ? ORDER BY id DESC LIMIT ? OFFSET ?";

    private static final String SEARCH_SQL =
            SELECT_BASE + " WHERE (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) ORDER BY id DESC LIMIT ? OFFSET ?";

    private static final String SEARCH_WITH_CATEGORY_SQL =
            SELECT_BASE + " WHERE (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) AND category = ? ORDER BY id DESC LIMIT ? OFFSET ?";

    private static final String INSERT_PRODUCT_SQL =
            "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_PRODUCT_SQL =
            "UPDATE products SET name = ?, description = ?, price = ?, stock_qty = ?, category = ?, image_url = ? WHERE id = ?";

    private static final String DELETE_PRODUCT_SQL =
            "DELETE FROM products WHERE id = ?";

    private static final String FIND_BY_SELLER_SQL =
            SELECT_BASE + " WHERE seller_id = ? ORDER BY id DESC LIMIT ? OFFSET ?";

    @Override
    public Optional<Product> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProduct(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding product by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Failed to query product by id", e);
        }
    }

    @Override
    public List<Product> findAll(int offset, int limit) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL)) {

            ps.setInt(1, limit > 0 ? limit : 20);
            ps.setInt(2, Math.max(offset, 0));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            logger.error("Error finding all products: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to list products", e);
        }
    }

    @Override
    public List<Product> findByCategory(String category, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_CATEGORY_SQL)) {

            ps.setString(1, category.trim());
            ps.setInt(2, limit > 0 ? limit : 20);
            ps.setInt(3, Math.max(offset, 0));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            logger.error("Error finding products by category {}: {}", category, e.getMessage(), e);
            throw new DatabaseException("Failed to filter products by category", e);
        }
    }

    @Override
    public List<Product> search(String keyword, String category, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        String wildcard = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
        boolean hasCategory = (category != null && !category.trim().isEmpty());

        String sql = hasCategory ? SEARCH_WITH_CATEGORY_SQL : SEARCH_SQL;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            int paramIndex = 3;
            if (hasCategory) {
                ps.setString(paramIndex++, category.trim());
            }
            ps.setInt(paramIndex++, limit > 0 ? limit : 20);
            ps.setInt(paramIndex, Math.max(offset, 0));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            logger.error("Error searching products with keyword '{}': {}", keyword, e.getMessage(), e);
            throw new DatabaseException("Failed to search products", e);
        }
    }

    @Override
    public Product create(Product product) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_PRODUCT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = (product.getCreatedAt() != null) ? product.getCreatedAt() : new Timestamp(System.currentTimeMillis());
            ps.setLong(1, product.getSellerId());
            ps.setString(2, product.getName().trim());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setString(6, product.getCategory().trim());
            ps.setString(7, product.getImageUrl());
            ps.setTimestamp(8, now);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                    product.setCreatedAt(now);
                } else {
                    throw new DatabaseException("Creating product failed, no ID obtained.");
                }
            }
            return product;
        } catch (SQLException e) {
            logger.error("Error inserting product '{}': {}", product.getName(), e.getMessage(), e);
            throw new DatabaseException("Failed to insert product", e);
        }
    }

    @Override
    public boolean update(Product product) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PRODUCT_SQL)) {

            ps.setString(1, product.getName().trim());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQty());
            ps.setString(5, product.getCategory().trim());
            ps.setString(6, product.getImageUrl());
            ps.setLong(7, product.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating product id {}: {}", product.getId(), e.getMessage(), e);
            throw new DatabaseException("Failed to update product", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_PRODUCT_SQL)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting product id {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Failed to delete product", e);
        }
    }

    @Override
    public List<Product> findBySellerId(Long sellerId, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_SELLER_SQL)) {

            ps.setLong(1, sellerId);
            ps.setInt(2, limit > 0 ? limit : 20);
            ps.setInt(3, Math.max(offset, 0));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            logger.error("Error finding products by seller id {}: {}", sellerId, e.getMessage(), e);
            throw new DatabaseException("Failed to list seller products", e);
        }
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
