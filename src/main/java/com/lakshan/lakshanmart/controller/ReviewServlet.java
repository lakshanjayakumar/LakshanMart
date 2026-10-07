package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dao.ReviewDAOImpl;
import com.lakshan.lakshanmart.dao.UserDAOImpl;
import com.lakshan.lakshanmart.dto.ReviewCreateRequest;
import com.lakshan.lakshanmart.dto.ReviewResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.ReviewService;
import com.lakshan.lakshanmart.service.ReviewServiceImpl;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST endpoint for product reviews and ratings (R2025 F8).
 * Endpoints: /api/v1/reviews/*
 */
@WebServlet(name = "ReviewServlet", urlPatterns = "/api/v1/reviews/*")
public class ReviewServlet extends BaseServlet {

    private final ReviewService reviewService;

    public ReviewServlet() {
        this.reviewService = new ReviewServiceImpl(new ReviewDAOImpl(), new OrderDAOImpl(), new ProductDAOImpl(), new UserDAOImpl());
    }

    public ReviewServlet(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }

        try {
            if (pathInfo.startsWith("/product/")) {
                Long productId = Long.parseLong(pathInfo.substring("/product/".length()));
                List<ReviewResponseDTO> reviews = reviewService.getProductReviews(productId);
                sendSuccess(resp, HttpServletResponse.SC_OK, reviews);
            } else if (pathInfo.startsWith("/can-review/")) {
                UserResponseDTO currentUser = getCurrentUser(req);
                Long productId = Long.parseLong(pathInfo.substring("/can-review/".length()));
                boolean canReview = currentUser != null && reviewService.canUserReview(currentUser.getId(), productId);
                sendSuccess(resp, HttpServletResponse.SC_OK, Map.of("canReview", canReview));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "ENDPOINT_NOT_FOUND", "Endpoint not found: " + pathInfo);
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Product ID must be numeric.");
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to submit a review.");
                return;
            }

            ReviewCreateRequest reviewReq = JsonUtil.fromJson(req.getReader(), ReviewCreateRequest.class);
            ReviewResponseDTO created = reviewService.addReview(currentUser.getId(), reviewReq);
            sendSuccess(resp, HttpServletResponse.SC_CREATED, created);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }
}
