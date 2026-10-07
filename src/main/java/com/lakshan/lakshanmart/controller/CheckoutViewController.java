package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.CartResponseDTO;
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

/**
 * Controller serving the checkout view (/checkout) and order confirmation view (/order-confirmation).
 * Requires authenticated session.
 */
@WebServlet(name = "CheckoutViewController", urlPatterns = {"/checkout", "/order-confirmation"})
public class CheckoutViewController extends BaseServlet {

    private final CartService cartService;
    private final OrderService orderService;

    public CheckoutViewController() {
        this(new CartServiceImpl(new CartDAOImpl(), new ProductDAOImpl()),
             new OrderServiceImpl(new OrderDAOImpl(), new CartDAOImpl(), new ProductDAOImpl()));
    }

    public CheckoutViewController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getContextPath() + req.getServletPath());
            return;
        }

        String path = req.getServletPath();
        if ("/order-confirmation".equals(path)) {
            handleConfirmation(req, resp, currentUser);
        } else {
            handleCheckout(req, resp, currentUser);
        }
    }

    private void handleCheckout(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser)
            throws ServletException, IOException {
        try {
            CartResponseDTO cart = cartService.getCart(currentUser.getId());
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }

            req.setAttribute("cart", cart);
            req.setAttribute("cartItemCount", cart.getItemCount());
            req.getRequestDispatcher("/WEB-INF/views/checkout/checkout.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleConfirmation(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO currentUser)
            throws ServletException, IOException {
        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        try {
            Long orderId = Long.parseLong(orderIdStr.trim());
            OrderResponseDTO order = orderService.getOrderDetails(currentUser.getId(), currentUser.getRole(), orderId);
            req.setAttribute("order", order);
            req.setAttribute("cartItemCount", 0);
            req.getRequestDispatcher("/WEB-INF/views/checkout/confirmation.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/orders");
        }
    }
}
