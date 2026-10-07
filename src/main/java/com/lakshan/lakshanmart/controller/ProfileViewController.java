package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.CartService;
import com.lakshan.lakshanmart.service.CartServiceImpl;
import com.lakshan.lakshanmart.service.OrderService;
import com.lakshan.lakshanmart.service.OrderServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller serving the User Profile and Account Overview (/profile).
 * Displays user info and order history in Amazon/Flipkart style.
 */
@WebServlet(name = "ProfileViewController", urlPatterns = {"/profile"})
public class ProfileViewController extends BaseServlet {

    private final OrderService orderService;
    private final CartService cartService;

    public ProfileViewController() {
        this(new OrderServiceImpl(new OrderDAOImpl(), new CartDAOImpl(), new ProductDAOImpl()),
             new CartServiceImpl(new CartDAOImpl(), new ProductDAOImpl()));
    }

    public ProfileViewController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getContextPath() + "/profile");
            return;
        }

        try {
            List<OrderResponseDTO> orders = orderService.getUserOrders(currentUser.getId());
            req.setAttribute("user", currentUser);
            req.setAttribute("orders", orders);
            req.setAttribute("totalOrders", orders.size());

            try {
                req.setAttribute("cartItemCount", cartService.getCart(currentUser.getId()).getItemCount());
            } catch (Exception ignored) {
                req.setAttribute("cartItemCount", 0);
            }

            req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, resp);
        }
    }
}
