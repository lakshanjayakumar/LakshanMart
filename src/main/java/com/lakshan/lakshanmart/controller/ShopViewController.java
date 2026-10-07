package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dao.ReviewDAOImpl;
import com.lakshan.lakshanmart.dao.UserDAOImpl;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.ReviewResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.CartService;
import com.lakshan.lakshanmart.service.CartServiceImpl;
import com.lakshan.lakshanmart.service.ProductService;
import com.lakshan.lakshanmart.service.ProductServiceImpl;
import com.lakshan.lakshanmart.service.ReviewService;
import com.lakshan.lakshanmart.service.ReviewServiceImpl;
import com.lakshan.lakshanmart.service.UserService;
import com.lakshan.lakshanmart.service.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Controller serving customer product catalog (/shop, /catalog, /category) and product details (/product).
 * Fully resilient against missing params to prevent 500 Internal Server Errors.
 */
@WebServlet(name = "ShopViewController", urlPatterns = {
        "/shop",
        "/catalog",
        "/products",
        "/category",
        "/category/*",
        "/product",
        "/product/*"
})
public class ShopViewController extends BaseServlet {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final CartService cartService;
    private final UserService userService;

    public ShopViewController() {
        this(new ProductServiceImpl(new ProductDAOImpl()),
             new ReviewServiceImpl(new ReviewDAOImpl(), new OrderDAOImpl(), new ProductDAOImpl(), new UserDAOImpl()),
             new CartServiceImpl(new CartDAOImpl(), new ProductDAOImpl()),
             new UserServiceImpl(new UserDAOImpl()));
    }

    public ShopViewController(ProductService productService, ReviewService reviewService, CartService cartService) {
        this(productService, reviewService, cartService, new UserServiceImpl(new UserDAOImpl()));
    }

    public ShopViewController(ProductService productService, ReviewService reviewService, CartService cartService, UserService userService) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser != null) {
            try {
                int count = cartService.getCart(currentUser.getId()).getItemCount();
                req.setAttribute("cartItemCount", count);
            } catch (Exception ignored) {
                req.setAttribute("cartItemCount", 0);
            }
        }

        if ("/product".equals(servletPath) || servletPath.startsWith("/product")) {
            handleProductDetail(req, resp, currentUser, pathInfo);
        } else if ("/category".equals(servletPath) || servletPath.startsWith("/category")) {
            handleCategory(req, resp, pathInfo);
        } else {
            handleCatalog(req, resp);
        }
    }

    private void handleCategory(HttpServletRequest req, HttpServletResponse resp, String pathInfo)
            throws ServletException, IOException {
        String category = req.getParameter("category");
        if (category == null || category.trim().isEmpty()) {
            category = req.getParameter("name");
        }

        if ((category == null || category.trim().isEmpty()) && pathInfo != null && pathInfo.length() > 1) {
            try {
                category = URLDecoder.decode(pathInfo.substring(1).trim(), StandardCharsets.UTF_8.name());
            } catch (Exception ignored) {
                category = pathInfo.substring(1).trim();
            }
        }

        List<ProductResponseDTO> products;
        if (category != null && !category.trim().isEmpty()) {
            products = productService.listProducts(category.trim(), 0, 100);
            req.setAttribute("currentCategory", category.trim());
        } else {
            products = productService.listProducts(null, 0, 100);
        }

        req.setAttribute("products", products != null ? products : Collections.emptyList());
        req.getRequestDispatcher("/WEB-INF/views/shop/catalog.jsp").forward(req, resp);
    }

    private void handleCatalog(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q");
        String category = req.getParameter("category");

        List<ProductResponseDTO> products;
        try {
            if (q != null && !q.trim().isEmpty()) {
                products = productService.searchProducts(q.trim(), category, 0, 100);
                req.setAttribute("searchQuery", q.trim());
                if (category != null && !category.trim().isEmpty()) {
                    req.setAttribute("currentCategory", category.trim());
                }
            } else if (category != null && !category.trim().isEmpty()) {
                products = productService.listProducts(category.trim(), 0, 100);
                req.setAttribute("currentCategory", category.trim());
            } else {
                products = productService.listProducts(null, 0, 100);
            }
        } catch (Exception e) {
            products = Collections.emptyList();
            req.setAttribute("errorMessage", "Unable to load products. Please try again.");
        }

        req.setAttribute("products", products != null ? products : Collections.emptyList());
        req.getRequestDispatcher("/WEB-INF/views/shop/catalog.jsp").forward(req, resp);
    }

    private void handleProductDetail(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser, String pathInfo)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if ((idParam == null || idParam.trim().isEmpty()) && pathInfo != null && pathInfo.length() > 1) {
            idParam = pathInfo.substring(1).trim();
        }

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/shop");
            return;
        }

        try {
            Long productId = Long.parseLong(idParam.trim());
            ProductResponseDTO product = productService.getProductById(productId);
            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/shop");
                return;
            }

            List<ReviewResponseDTO> reviews = reviewService.getProductReviews(productId);

            boolean canReview = false;
            if (currentUser != null) {
                canReview = reviewService.canUserReview(currentUser.getId(), productId);
            }

            // Fetch seller name for the product
            String sellerName = "LakshanMart Assured Merchant";
            if (product.getSellerId() != null) {
                try {
                    UserResponseDTO seller = userService.getUserById(product.getSellerId());
                    if (seller != null && seller.getName() != null) {
                        sellerName = seller.getName();
                    }
                } catch (Exception ignored) {
                }
            }

            req.setAttribute("product", product);
            req.setAttribute("sellerName", sellerName);
            req.setAttribute("reviews", reviews != null ? reviews : Collections.emptyList());
            req.setAttribute("canReview", canReview);

            req.getRequestDispatcher("/WEB-INF/views/shop/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/shop");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/shop");
        }
    }
}
