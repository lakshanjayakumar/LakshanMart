package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.OrderDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.dto.OrderStatusUpdateRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.service.OrderService;
import com.lakshan.lakshanmart.service.OrderServiceImpl;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST endpoint for administrative order management.
 * Protected for ADMIN role.
 * Endpoints: /api/v1/admin/orders/*
 */
@WebServlet(name = "AdminOrderServlet", urlPatterns = "/api/v1/admin/orders/*")
public class AdminOrderServlet extends BaseServlet {

    private final OrderService orderService;

    public AdminOrderServlet() {
        this.orderService = new OrderServiceImpl(new OrderDAOImpl(), new CartDAOImpl(), new ProductDAOImpl());
    }

    public AdminOrderServlet(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null || !Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Admin access required.");
                return;
            }

            List<OrderResponseDTO> allOrders = orderService.getAllOrders();
            sendSuccess(resp, HttpServletResponse.SC_OK, allOrders);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null || !Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Admin access required.");
                return;
            }

            Long orderId = extractIdFromPath(req.getPathInfo());
            OrderStatusUpdateRequest statusReq = JsonUtil.fromJson(req.getReader(), OrderStatusUpdateRequest.class);

            if (orderId == null || statusReq == null || statusReq.getStatus() == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "Order ID and new status required.");
                return;
            }

            OrderResponseDTO updatedOrder = orderService.updateOrderStatus(orderId, statusReq.getStatus());
            sendSuccess(resp, HttpServletResponse.SC_OK, updatedOrder);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private Long extractIdFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            return null;
        }
        String cleaned = pathInfo.replaceAll("^/+", "");
        // If path is e.g. "12/status" or just "12"
        String[] parts = cleaned.split("/");
        try {
            return Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
