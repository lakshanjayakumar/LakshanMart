package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.CheckoutRequest;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.OrderService;
import com.lakshan.lakshanmart.service.OrderServiceImpl;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST endpoint for customer order placement and history tracking.
 * Endpoints: /api/v1/orders/*
 */
@WebServlet(name = "OrderServlet", urlPatterns = "/api/v1/orders/*")
public class OrderServlet extends BaseServlet {

    private final OrderService orderService;

    public OrderServlet() {
        this.orderService = new OrderServiceImpl(new OrderDAOImpl(), new CartDAOImpl(), new ProductDAOImpl());
    }

    public OrderServlet(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }

        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to place orders.");
                return;
            }

            if ("/checkout".equals(pathInfo) || "/place".equals(pathInfo)) {
                CheckoutRequest checkoutReq = JsonUtil.fromJson(req.getReader(), CheckoutRequest.class);
                OrderResponseDTO order = orderService.checkout(currentUser.getId(), checkoutReq);
                sendSuccess(resp, HttpServletResponse.SC_CREATED, order);
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "ENDPOINT_NOT_FOUND", "Order endpoint not found: " + pathInfo);
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }

        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to view orders.");
                return;
            }

            if (pathInfo.isEmpty() || "/".equals(pathInfo) || "/my-orders".equals(pathInfo)) {
                List<OrderResponseDTO> orders = orderService.getUserOrders(currentUser.getId());
                sendSuccess(resp, HttpServletResponse.SC_OK, orders);
            } else {
                String idStr = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
                try {
                    Long orderId = Long.parseLong(idStr);
                    OrderResponseDTO order = orderService.getOrderDetails(currentUser.getId(), currentUser.getRole(), orderId);
                    sendSuccess(resp, HttpServletResponse.SC_OK, order);
                } catch (NumberFormatException e) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Order ID must be numeric.");
                }
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }
}
