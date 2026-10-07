package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.ProductCreateRequest;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.ProductUpdateRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.ProductService;
import com.lakshan.lakshanmart.service.ProductServiceImpl;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet handling administrative and seller product CRUD operations.
 * Protected by AuthFilter for ADMIN and SELLER roles.
 * Endpoints: /api/v1/admin/products/*
 */
@WebServlet(name = "AdminProductServlet", urlPatterns = "/api/v1/admin/products/*")
public class AdminProductServlet extends BaseServlet {

    private final ProductService productService;

    public AdminProductServlet() {
        this.productService = new ProductServiceImpl(new ProductDAOImpl());
    }

    public AdminProductServlet(ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int offset = parseInteger(req.getParameter("offset"), 0);
            int limit = parseInteger(req.getParameter("limit"), 50);
            String category = req.getParameter("category");

            List<ProductResponseDTO> products = productService.listProducts(category, offset, limit);
            sendSuccess(resp, HttpServletResponse.SC_OK, products);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
                return;
            }

            ProductCreateRequest createReq = JsonUtil.fromJson(req.getReader(), ProductCreateRequest.class);
            ProductResponseDTO created = productService.createProduct(currentUser.getId(), createReq);
            sendSuccess(resp, HttpServletResponse.SC_CREATED, created);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
                return;
            }

            Long productId = extractIdFromPath(req.getPathInfo());
            ProductUpdateRequest updateReq = JsonUtil.fromJson(req.getReader(), ProductUpdateRequest.class);
            if (productId != null) {
                updateReq.setId(productId);
            }

            ProductResponseDTO updated = productService.updateProduct(currentUser.getId(), currentUser.getRole(), updateReq);
            sendSuccess(resp, HttpServletResponse.SC_OK, updated);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
                return;
            }

            Long productId = extractIdFromPath(req.getPathInfo());
            if (productId == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Product ID must be specified in the URL.");
                return;
            }

            productService.deleteProduct(currentUser.getId(), currentUser.getRole(), productId);
            sendSuccess(resp, HttpServletResponse.SC_OK, Map.of("message", "Product deleted successfully", "id", productId));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private Long extractIdFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            return null;
        }
        String idStr = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseInteger(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
