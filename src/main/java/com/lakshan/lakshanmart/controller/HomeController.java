package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.CartDAOImpl;
import com.lakshan.lakshanmart.dao.ProductDAOImpl;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.service.CartService;
import com.lakshan.lakshanmart.service.CartServiceImpl;
import com.lakshan.lakshanmart.service.ProductService;
import com.lakshan.lakshanmart.service.ProductServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller serving the marketplace homepage and storefront.
 */
@WebServlet(name = "HomeController", urlPatterns = {"", "/home", "/index"})
public class HomeController extends BaseServlet {

    private final ProductService productService;
    private final CartService cartService;

    public HomeController() {
        this(new ProductServiceImpl(new ProductDAOImpl()),
             new CartServiceImpl(new CartDAOImpl(), new ProductDAOImpl()));
    }

    public HomeController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ProductResponseDTO> featured = productService.listProducts(null, 0, 8);
            req.setAttribute("featuredProducts", featured);

            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser != null) {
                try {
                    int count = cartService.getCart(currentUser.getId()).getItemCount();
                    req.setAttribute("cartItemCount", count);
                } catch (Exception e) {
                    req.setAttribute("cartItemCount", 0);
                }
            }

            req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading catalog: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
        }
    }
}
