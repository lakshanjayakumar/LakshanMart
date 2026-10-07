package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.exception.DatabaseException;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.model.User;
import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDAO.
 * All SQL statements strictly use PreparedStatement and try-with-resources.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private static final String FIND_BY_ID_SQL =
            "SELECT id, name, email, password_hash, role, created_at FROM users WHERE id = ?";

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";

    private static final String EXISTS_BY_EMAIL_SQL =
            "SELECT 1 FROM users WHERE email = ?";

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (name, email, password_hash, role, created_at) VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_ALL_SQL =
            "SELECT id, name, email, password_hash, role, created_at FROM users ORDER BY id ASC";

    private static final String UPDATE_ROLE_SQL =
            "UPDATE users SET role = ? WHERE id = ?";

    @Override
    public Optional<User> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Failed to query user by id", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage(), e);
            throw new DatabaseException("Failed to query user by email", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_BY_EMAIL_SQL)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking existence for email {}: {}", email, e.getMessage(), e);
            throw new DatabaseException("Failed to verify user email existence", e);
        }
    }

    @Override
    public User create(User user) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = (user.getCreatedAt() != null) ? user.getCreatedAt() : new Timestamp(System.currentTimeMillis());
            ps.setString(1, user.getName().trim());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setTimestamp(5, now);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    user.setCreatedAt(now);
                } else {
                    throw new DatabaseException("Creating user failed, no ID obtained.");
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Error creating user {}: {}", user.getEmail(), e.getMessage(), e);
            throw new DatabaseException("Failed to insert user into database", e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve user list", e);
        }
    }

    @Override
    public boolean updateRole(Long userId, String role) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ROLE_SQL)) {

            ps.setString(1, role);
            ps.setLong(2, userId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Error updating role to {} for user {}: {}", role, userId, e.getMessage(), e);
            throw new DatabaseException("Failed to update user role", e);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
