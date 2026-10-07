package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.OrderDAO;
import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dao.ReviewDAO;
import com.lakshan.lakshanmart.dao.UserDAO;
import com.lakshan.lakshanmart.dto.ReviewCreateRequest;
import com.lakshan.lakshanmart.dto.ReviewResponseDTO;
import com.lakshan.lakshanmart.exception.ConflictException;
import com.lakshan.lakshanmart.exception.ForbiddenException;
import com.lakshan.lakshanmart.exception.ResourceNotFoundException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.Review;
import com.lakshan.lakshanmart.model.User;
import com.lakshan.lakshanmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ReviewService enforcing verified purchase restrictions (R2025 F8).
 */
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;

    public ReviewServiceImpl(ReviewDAO reviewDAO, OrderDAO orderDAO, ProductDAO productDAO, UserDAO userDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.userDAO = userDAO;
    }

    @Override
    public ReviewResponseDTO addReview(Long userId, ReviewCreateRequest request) {
        ValidationUtil.requirePositiveId(userId, "User");
        if (request == null) {
            throw new ValidationException("Review payload cannot be null.");
        }
        ValidationUtil.requirePositiveId(request.getProductId(), "Product");

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ValidationException("Rating must be an integer between 1 and 5.");
        }
        ValidationUtil.requireNonBlank(request.getComment(), "Review comment");

        productDAO.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // R2025 F8 Specification: Product reviews and star ratings on completed orders
        boolean hasPurchased = orderDAO.hasPurchasedProduct(userId, request.getProductId());
        if (!hasPurchased) {
            logger.warn("User {} attempted to review product {} without a completed purchase", userId, request.getProductId());
            throw new ForbiddenException("Only verified buyers who have completed an order for this product can leave a review.");
        }

        boolean alreadyReviewed = reviewDAO.hasUserReviewedProduct(userId, request.getProductId());
        if (alreadyReviewed) {
            throw new ConflictException("You have already reviewed this product.");
        }

        Review review = new Review();
        review.setProductId(request.getProductId());
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());

        Review created = reviewDAO.create(review);
        created.setUserName(user.getName());
        logger.info("Review {} added for product {} by user {}", created.getId(), request.getProductId(), userId);

        return toReviewResponseDTO(created);
    }

    @Override
    public List<ReviewResponseDTO> getProductReviews(Long productId) {
        ValidationUtil.requirePositiveId(productId, "Product");
        List<Review> reviews = reviewDAO.findByProductId(productId);
        return reviews.stream().map(this::toReviewResponseDTO).collect(Collectors.toList());
    }

    @Override
    public boolean canUserReview(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }
        return orderDAO.hasPurchasedProduct(userId, productId) && !reviewDAO.hasUserReviewedProduct(userId, productId);
    }

    private ReviewResponseDTO toReviewResponseDTO(Review r) {
        return new ReviewResponseDTO(
                r.getId(),
                r.getProductId(),
                r.getUserId(),
                r.getUserName() != null ? r.getUserName() : "Verified Buyer",
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
