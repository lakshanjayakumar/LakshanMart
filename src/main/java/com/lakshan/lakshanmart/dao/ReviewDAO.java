package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.Review;

import java.util.List;

/**
 * Data Access Object interface for product reviews and ratings.
 */
public interface ReviewDAO {

    /**
     * Lists all reviews for a product joined with reviewer name, ordered newest first.
     */
    List<Review> findByProductId(Long productId);

    /**
     * Persists a new product review and rating.
     */
    Review create(Review review);

    /**
     * Checks if a user has already reviewed a given product.
     */
    boolean hasUserReviewedProduct(Long userId, Long productId);
}
