package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.CartResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.CartService;
import com.lakshan.lakshanmart.service.CartServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

/**
 * Controller serving the customer shopping cart view (/cart).
 */
@WebServlet(name = "CartViewController", urlPatterns = {"/cart"})
public class CartViewController extends BaseServlet {

    private final CartService cartService;

    public CartViewController() {
        this(new CartServiceImpl(new CartDAOImpl(), new ProductDAOImpl()));
    }

    public CartViewController(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            req.setAttribute("cart", new CartResponseDTO(Collections.emptyList(), BigDecimal.ZERO, 0));
            req.setAttribute("cartItemCount", 0);
        } else {
            try {
                CartResponseDTO cart = cartService.getCart(currentUser.getId());
                req.setAttribute("cart", cart);
                req.setAttribute("cartItemCount", cart.getItemCount());
            } catch (Exception e) {
                req.setAttribute("cart", new CartResponseDTO(Collections.emptyList(), BigDecimal.ZERO, 0));
                req.setAttribute("cartItemCount", 0);
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/cart/cart.jsp").forward(req, resp);
    }
}
