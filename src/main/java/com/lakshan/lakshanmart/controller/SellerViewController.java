package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dao.UserDAOImpl;
import com.lakshan.lakshanmart.dto.ProductCreateRequest;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.ProductUpdateRequest;
import com.lakshan.lakshanmart.dto.SellerOrderItemDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.service.OrderService;
import com.lakshan.lakshanmart.service.OrderServiceImpl;
import com.lakshan.lakshanmart.service.ProductService;
import com.lakshan.lakshanmart.service.ProductServiceImpl;
import com.lakshan.lakshanmart.service.UserService;
import com.lakshan.lakshanmart.service.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller serving Seller Portal views and operations:
 * - Onboarding / "Become a Seller" (/seller/onboard)
 * - Seller Dashboard (/seller, /seller/dashboard)
 * - Product additions, edits, and deletions for seller-owned inventory
 * - Order tracking and earnings analytics for seller products
 */
@WebServlet(name = "SellerViewController", urlPatterns = {
        "/seller",
        "/seller/dashboard",
        "/seller/onboard",
        "/seller/product/create",
        "/seller/product/update",
        "/seller/product/delete"
})
public class SellerViewController extends BaseServlet {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    public SellerViewController() {
        this(new ProductServiceImpl(new ProductDAOImpl()),
             new OrderServiceImpl(new OrderDAOImpl(), new CartDAOImpl(), new ProductDAOImpl()),
             new UserServiceImpl(new UserDAOImpl()));
    }

    public SellerViewController(ProductService productService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getContextPath() + req.getServletPath());
            return;
        }

        String path = req.getServletPath();

        if ("/seller/onboard".equals(path)) {
            if (Role.SELLER.name().equalsIgnoreCase(currentUser.getRole()) ||
                Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/seller/onboard.jsp").forward(req, resp);
            return;
        }

        // If not seller or admin, prompt to onboard
        boolean isSeller = Role.SELLER.name().equalsIgnoreCase(currentUser.getRole()) ||
                           Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole());
        if (!isSeller) {
            resp.sendRedirect(req.getContextPath() + "/seller/onboard");
            return;
        }

        handleSellerDashboard(req, resp, currentUser);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/seller/onboard".equals(path)) {
            handleOnboardSubmit(req, resp, currentUser);
        } else if ("/seller/product/create".equals(path)) {
            handleProductCreate(req, resp, currentUser);
        } else if ("/seller/product/update".equals(path)) {
            handleProductUpdate(req, resp, currentUser);
        } else if ("/seller/product/delete".equals(path)) {
            handleProductDelete(req, resp, currentUser);
        } else {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        }
    }

    private void handleSellerDashboard(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser)
            throws ServletException, IOException {
        try {
            List<ProductResponseDTO> products = productService.getProductsBySeller(currentUser.getId(), 0, 100);
            List<SellerOrderItemDTO> soldItems = orderService.getSellerOrderItems(currentUser.getId());

            BigDecimal totalEarnings = BigDecimal.ZERO;
            int totalUnitsSold = 0;
            Set<Long> uniqueOrders = new HashSet<>();

            if (soldItems != null) {
                for (SellerOrderItemDTO item : soldItems) {
                    if (item.getSubtotal() != null) {
                        totalEarnings = totalEarnings.add(item.getSubtotal());
                    }
                    totalUnitsSold += item.getQuantity();
                    if (item.getOrderId() != null) {
                        uniqueOrders.add(item.getOrderId());
                    }
                }
            }

            req.setAttribute("products", products != null ? products : Collections.emptyList());
            req.setAttribute("soldItems", soldItems != null ? soldItems : Collections.emptyList());
            req.setAttribute("totalProducts", products != null ? products.size() : 0);
            req.setAttribute("totalEarnings", totalEarnings);
            req.setAttribute("totalUnitsSold", totalUnitsSold);
            req.setAttribute("totalOrders", uniqueOrders.size());

            req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading seller dashboard: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
        }
    }

    private void handleOnboardSubmit(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser)
            throws IOException {
        try {
            UserResponseDTO updated = userService.updateUserRole(currentUser.getId(), Role.SELLER.name());
            req.getSession().setAttribute("currentUser", updated);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?onboarded=true");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/onboard?error=" + e.getMessage());
        }
    }

    private void handleProductCreate(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser)
            throws IOException {
        try {
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String category = req.getParameter("category");
            String imageUrl = req.getParameter("imageUrl");
            BigDecimal price = new BigDecimal(req.getParameter("price").trim());
            int stockQty = Integer.parseInt(req.getParameter("stockQty").trim());

            ProductCreateRequest createReq = new ProductCreateRequest(name, description, price, stockQty, category, imageUrl);
            productService.createProduct(currentUser.getId(), createReq);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=Product+listed+successfully");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=" + e.getMessage());
        }
    }

    private void handleProductUpdate(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser)
            throws IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id").trim());
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String category = req.getParameter("category");
            String imageUrl = req.getParameter("imageUrl");
            BigDecimal price = new BigDecimal(req.getParameter("price").trim());
            int stockQty = Integer.parseInt(req.getParameter("stockQty").trim());

            ProductUpdateRequest updateReq = new ProductUpdateRequest(id, name, description, price, stockQty, category, imageUrl);
            productService.updateProduct(currentUser.getId(), currentUser.getRole(), updateReq);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=Product+updated+successfully");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=" + e.getMessage());
        }
    }

    private void handleProductDelete(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser)
            throws IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id").trim());
            productService.deleteProduct(currentUser.getId(), currentUser.getRole(), id);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=Product+deleted+successfully");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=" + e.getMessage());
        }
    }
}
