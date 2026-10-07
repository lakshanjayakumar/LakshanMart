package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.exception.DatabaseException;
import com.lakshan.lakshanmart.model.Review;
import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ReviewDAO using PreparedStatements and try-with-resources.
 */
public class ReviewDAOImpl implements ReviewDAO {

    private static final Logger logger = LoggerFactory.getLogger(ReviewDAOImpl.class);

    private static final String FIND_BY_PRODUCT_SQL =
            "SELECT r.id, r.product_id, r.user_id, r.rating, r.comment, r.created_at, u.name AS user_name " +
            "FROM reviews r " +
            "JOIN users u ON r.user_id = u.id " +
            "WHERE r.product_id = ? " +
            "ORDER BY r.id DESC";

    private static final String INSERT_REVIEW_SQL =
            "INSERT INTO reviews (product_id, user_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?)";

    private static final String HAS_REVIEWED_SQL =
            "SELECT 1 FROM reviews WHERE user_id = ? AND product_id = ? LIMIT 1";

    @Override
    public List<Review> findByProductId(Long productId) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PRODUCT_SQL)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setUserName(rs.getString("user_name"));
                    reviews.add(r);
                }
            }
            return reviews;
        } catch (SQLException e) {
            logger.error("Error retrieving reviews for product {}: {}", productId, e.getMessage(), e);
            throw new DatabaseException("Failed to retrieve product reviews", e);
        }
    }

    @Override
    public Review create(Review review) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_REVIEW_SQL, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = (review.getCreatedAt() != null) ? review.getCreatedAt() : new Timestamp(System.currentTimeMillis());
            ps.setLong(1, review.getProductId());
            ps.setLong(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment() != null ? review.getComment().trim() : "");
            ps.setTimestamp(5, now);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to insert review, no rows affected");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    review.setId(keys.getLong(1));
                    review.setCreatedAt(now);
                }
            }
            return review;
        } catch (SQLException e) {
            logger.error("Error creating review for product {}: {}", review.getProductId(), e.getMessage(), e);
            throw new DatabaseException("Failed to submit review", e);
        }
    }

    @Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(HAS_REVIEWED_SQL)) {

            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking review status for user {} on product {}: {}", userId, productId, e.getMessage(), e);
            throw new DatabaseException("Failed to check review status", e);
        }
    }
}
