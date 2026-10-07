package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dto.ReviewCreateRequest;
import com.lakshan.lakshanmart.dto.ReviewResponseDTO;

import java.util.List;

/**
 * Service interface for product reviews and ratings.
 */
public interface ReviewService {

    /**
     * Submits a verified buyer product review, checking for completed purchase.
     */
    ReviewResponseDTO addReview(Long userId, ReviewCreateRequest request);

    /**
     * Lists all reviews for a product.
     */
    List<ReviewResponseDTO> getProductReviews(Long productId);

    /**
     * Checks if a user is eligible to review a product (has purchased it and hasn't reviewed yet).
     */
    boolean canUserReview(Long userId, Long productId);
}
