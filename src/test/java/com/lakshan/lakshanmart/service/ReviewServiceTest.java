package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.OrderDAO;
import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dao.ReviewDAO;
import com.lakshan.lakshanmart.dao.UserDAO;
import com.lakshan.lakshanmart.dto.ReviewCreateRequest;
import com.lakshan.lakshanmart.dto.ReviewResponseDTO;
import com.lakshan.lakshanmart.exception.ConflictException;
import com.lakshan.lakshanmart.exception.ForbiddenException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.Product;
import com.lakshan.lakshanmart.model.Review;
import com.lakshan.lakshanmart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewDAO reviewDAO;

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private ProductDAO productDAO;

    @Mock
    private UserDAO userDAO;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewDAO, orderDAO, productDAO, userDAO);
    }

    @Test
    @DisplayName("Review succeeds when user has purchased the product")
    void testAddReviewVerifiedBuyerSuccess() {
        Product p = new Product(10L, 2L, "Headphones", "Noise Cancelling",
                new BigDecimal("199.99"), 15, "Electronics", null, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(p));
        when(orderDAO.hasPurchasedProduct(1L, 10L)).thenReturn(true);
        when(reviewDAO.hasUserReviewedProduct(1L, 10L)).thenReturn(false);

        User buyer = new User();
        buyer.setId(1L);
        buyer.setName("Verified Buyer");
        when(userDAO.findById(1L)).thenReturn(Optional.of(buyer));

        when(reviewDAO.create(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(50L);
            r.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return r;
        });

        ReviewCreateRequest req = new ReviewCreateRequest(10L, 5, "Outstanding sound quality!");
        ReviewResponseDTO response = reviewService.addReview(1L, req);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals(5, response.getRating());
        assertEquals("Outstanding sound quality!", response.getComment());
        verify(reviewDAO).create(any(Review.class));
    }

    @Test
    @DisplayName("Review is rejected with ForbiddenException if user has not purchased the item (R2025 F8)")
    void testAddReviewNonBuyerForbidden() {
        Product p = new Product(10L, 2L, "Headphones", "Noise Cancelling",
                new BigDecimal("199.99"), 15, "Electronics", null, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(p));
        User buyer = new User();
        buyer.setId(1L);
        when(userDAO.findById(1L)).thenReturn(Optional.of(buyer));
        when(orderDAO.hasPurchasedProduct(1L, 10L)).thenReturn(false);

        ReviewCreateRequest req = new ReviewCreateRequest(10L, 4, "Looks cool");
        assertThrows(ForbiddenException.class, () -> reviewService.addReview(1L, req));

        verify(reviewDAO, never()).create(any(Review.class));
    }

    @Test
    @DisplayName("Duplicate review on same product throws ConflictException")
    void testDuplicateReviewConflict() {
        Product p = new Product(10L, 2L, "Headphones", "Noise Cancelling",
                new BigDecimal("199.99"), 15, "Electronics", null, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(p));
        User buyer = new User();
        buyer.setId(1L);
        when(userDAO.findById(1L)).thenReturn(Optional.of(buyer));
        when(orderDAO.hasPurchasedProduct(1L, 10L)).thenReturn(true);
        when(reviewDAO.hasUserReviewedProduct(1L, 10L)).thenReturn(true);

        ReviewCreateRequest req = new ReviewCreateRequest(10L, 5, "Second review");
        assertThrows(ConflictException.class, () -> reviewService.addReview(1L, req));
    }

    @Test
    @DisplayName("Review rejects invalid ratings outside 1 to 5")
    void testInvalidRating() {
        ReviewCreateRequest req = new ReviewCreateRequest(10L, 6, "Invalid");
        assertThrows(ValidationException.class, () -> reviewService.addReview(1L, req));
    }
}
