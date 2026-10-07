package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.AddToCartRequest;
import com.lakshan.lakshanmart.dto.CartResponseDTO;
import com.lakshan.lakshanmart.dto.UpdateCartRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.CartService;
import com.lakshan.lakshanmart.service.CartServiceImpl;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * REST endpoint for persistent shopping cart operations.
 * Endpoints: /api/v1/cart/*
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/v1/cart/*")
public class CartServlet extends BaseServlet {

    private final CartService cartService;

    public CartServlet() {
        this.cartService = new CartServiceImpl(new CartDAOImpl(), new ProductDAOImpl());
    }

    public CartServlet(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to view cart.");
                return;
            }

            CartResponseDTO cart = cartService.getCart(currentUser.getId());
            sendSuccess(resp, HttpServletResponse.SC_OK, cart);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to add items to cart.");
                return;
            }

            AddToCartRequest addReq = JsonUtil.fromJson(req.getReader(), AddToCartRequest.class);
            CartResponseDTO cart = cartService.addToCart(currentUser.getId(), addReq);
            sendSuccess(resp, HttpServletResponse.SC_OK, cart);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to update cart.");
                return;
            }

            Long cartItemId = extractIdFromPath(req.getPathInfo());
            UpdateCartRequest updateReq = JsonUtil.fromJson(req.getReader(), UpdateCartRequest.class);
            if (cartItemId == null && updateReq != null) {
                cartItemId = updateReq.getCartItemId();
            }

            if (cartItemId == null || updateReq == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "Cart item ID and quantity required.");
                return;
            }

            CartResponseDTO cart = cartService.updateCartItem(currentUser.getId(), cartItemId, updateReq.getQuantity());
            sendSuccess(resp, HttpServletResponse.SC_OK, cart);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to manage cart.");
                return;
            }

            Long cartItemId = extractIdFromPath(req.getPathInfo());
            CartResponseDTO cart;
            if (cartItemId != null) {
                cart = cartService.removeCartItem(currentUser.getId(), cartItemId);
            } else {
                cartService.clearCart(currentUser.getId());
                cart = cartService.getCart(currentUser.getId());
            }
            sendSuccess(resp, HttpServletResponse.SC_OK, cart);
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
}
