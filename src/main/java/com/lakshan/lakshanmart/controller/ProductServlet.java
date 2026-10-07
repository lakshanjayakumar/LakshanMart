package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.service.ProductService;
import com.lakshan.lakshanmart.service.ProductServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Public servlet for browsing, searching, and viewing product details.
 * Endpoints: /api/v1/products/*
 */
@WebServlet(name = "ProductServlet", urlPatterns = "/api/v1/products/*")
public class ProductServlet extends BaseServlet {

    private final ProductService productService;

    public ProductServlet() {
        this.productService = new ProductServiceImpl(new ProductDAOImpl());
    }

    public ProductServlet(ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }

        try {
            if (pathInfo.isEmpty() || "/".equals(pathInfo)) {
                // List products with category/offset/limit
                String category = req.getParameter("category");
                int offset = parseInteger(req.getParameter("offset"), 0);
                int limit = parseInteger(req.getParameter("limit"), 20);

                List<ProductResponseDTO> products = productService.listProducts(category, offset, limit);
                sendSuccess(resp, HttpServletResponse.SC_OK, products);
            } else if ("/search".equals(pathInfo)) {
                // Search products by keyword
                String keyword = req.getParameter("q");
                String category = req.getParameter("category");
                int offset = parseInteger(req.getParameter("offset"), 0);
                int limit = parseInteger(req.getParameter("limit"), 20);

                List<ProductResponseDTO> products = productService.searchProducts(keyword, category, offset, limit);
                sendSuccess(resp, HttpServletResponse.SC_OK, products);
            } else {
                // Product details by ID: /{id}
                String idStr = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
                try {
                    Long productId = Long.parseLong(idStr);
                    ProductResponseDTO product = productService.getProductById(productId);
                    sendSuccess(resp, HttpServletResponse.SC_OK, product);
                } catch (NumberFormatException e) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Product ID must be numeric.");
                }
            }
        } catch (Exception e) {
            handleException(resp, e);
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
