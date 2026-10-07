package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dao.UserDAOImpl;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller serving Admin Web UI views:
 * Dashboard (/admin), Product Management (/admin/products),
 * Order Management (/admin/orders), and User Directory (/admin/users).
 */
@WebServlet(name = "AdminViewController", urlPatterns = {
        "/admin",
        "/admin/dashboard",
        "/admin/products",
        "/admin/orders",
        "/admin/users",
        "/admin/users/role",
        "/admin/products/approve"
})
public class AdminViewController extends BaseServlet {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminViewController() {
        this(new ProductServiceImpl(new ProductDAOImpl()),
             new OrderServiceImpl(new OrderDAOImpl(), new CartDAOImpl(), new ProductDAOImpl()),
             new UserServiceImpl(new UserDAOImpl()));
    }

    public AdminViewController(ProductService productService, OrderService orderService, UserService userService) {
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

        if (!Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin privileges required to access this portal.");
            return;
        }

        String path = req.getServletPath();

        if ("/admin/products".equals(path)) {
            handleProducts(req, resp);
        } else if ("/admin/orders".equals(path)) {
            handleOrders(req, resp);
        } else if ("/admin/users".equals(path)) {
            handleUsers(req, resp);
        } else {
            handleDashboard(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser == null || !Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin privileges required.");
            return;
        }

        String path = req.getServletPath();
        if ("/admin/users/role".equals(path)) {
            try {
                Long userId = Long.parseLong(req.getParameter("userId").trim());
                String role = req.getParameter("role").trim();
                userService.updateUserRole(userId, role);
                resp.sendRedirect(req.getContextPath() + "/admin/users?success=User+role+updated+to+" + role);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=" + e.getMessage());
            }
        } else if ("/admin/products/approve".equals(path)) {
            resp.sendRedirect(req.getContextPath() + "/admin/products?success=Product+approved+successfully");
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin");
        }
    }

    private void handleDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ProductResponseDTO> products = productService.listProducts(null, 0, 500);
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        List<UserResponseDTO> users = userService.getAllUsers();

        BigDecimal totalRevenue = orders.stream()
                .map(OrderResponseDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalSellers = users.stream().filter(u -> "SELLER".equalsIgnoreCase(u.getRole())).count();
        long totalBuyers = users.stream().filter(u -> "BUYER".equalsIgnoreCase(u.getRole())).count();

        List<OrderResponseDTO> recentOrders = orders.size() > 5 ? orders.subList(0, 5) : orders;

        req.setAttribute("totalProducts", products.size());
        req.setAttribute("totalOrders", orders.size());
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("totalUsers", users.size());
        req.setAttribute("totalSellers", totalSellers);
        req.setAttribute("totalBuyers", totalBuyers);
        req.setAttribute("recentOrders", recentOrders);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void handleProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ProductResponseDTO> products = productService.listProducts(null, 0, 500);
        req.setAttribute("products", products);
        req.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(req, resp);
    }

    private void handleOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
    }

    private void handleUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String roleFilter = req.getParameter("role");
        List<UserResponseDTO> allUsers = userService.getAllUsers();

        long totalSellers = allUsers.stream().filter(u -> "SELLER".equalsIgnoreCase(u.getRole())).count();
        long totalBuyers = allUsers.stream().filter(u -> "BUYER".equalsIgnoreCase(u.getRole())).count();
        long totalAdmins = allUsers.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).count();

        List<UserResponseDTO> filtered = allUsers;
        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            filtered = allUsers.stream()
                    .filter(u -> roleFilter.trim().equalsIgnoreCase(u.getRole()))
                    .collect(Collectors.toList());
            req.setAttribute("currentRoleFilter", roleFilter.trim());
        }

        req.setAttribute("users", filtered);
        req.setAttribute("totalUsers", allUsers.size());
        req.setAttribute("totalSellers", totalSellers);
        req.setAttribute("totalBuyers", totalBuyers);
        req.setAttribute("totalAdmins", totalAdmins);

        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }
}
