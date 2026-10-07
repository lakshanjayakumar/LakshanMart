package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.exception.DatabaseException;
import com.lakshan.lakshanmart.model.Category;
import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CategoryDAO using HikariCP connection pool and PreparedStatements.
 */
public class CategoryDAOImpl implements CategoryDAO {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDAOImpl.class);

    private static final String SQL_FIND_ALL =
            "SELECT id, name, description, icon, created_at FROM categories ORDER BY id ASC";

    private static final String SQL_FIND_BY_ID =
            "SELECT id, name, description, icon, created_at FROM categories WHERE id = ?";

    private static final String SQL_FIND_BY_NAME =
            "SELECT id, name, description, icon, created_at FROM categories WHERE LOWER(name) = LOWER(?)";

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all categories: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve categories from database", e);
        }
        return list;
    }

    @Override
    public Optional<Category> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving category by ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve category by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Category> findByName(String name) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_NAME)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving category by name '{}': {}", name, e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve category by name", e);
        }
        return Optional.empty();
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        return new Category(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("icon"),
                rs.getTimestamp("created_at")
        );
    }
}
