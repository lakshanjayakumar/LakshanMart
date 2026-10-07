package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.CartResponseDTO;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.service.CartService;
import com.lakshan.lakshanmart.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HomeControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    private HomeController homeController;

    @BeforeEach
    void setUp() {
        homeController = new HomeController(productService, cartService);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    @DisplayName("GET /home forwards to home.jsp with featured products")
    void testGetHome() throws Exception {
        List<ProductResponseDTO> products = List.of(
                new ProductResponseDTO(1L, 2L, "Product 1", "Desc 1", new BigDecimal("10.00"), 5, "Electronics", null, null)
        );
        when(productService.listProducts(null, 0, 8)).thenReturn(products);

        homeController.doGet(request, response);

        verify(request).setAttribute(eq("featuredProducts"), eq(products));
        verify(request).getRequestDispatcher("/WEB-INF/views/home.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /home populates cartItemCount for logged in user")
    void testGetHomeWithLoggedInUser() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Bob", "bob@example.com", "BUYER", new Timestamp(System.currentTimeMillis()));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(AuthFilter.SESSION_USER_KEY)).thenReturn(user);

        CartResponseDTO cart = new CartResponseDTO(Collections.emptyList(), BigDecimal.ZERO, 3);
        when(cartService.getCart(1L)).thenReturn(cart);

        homeController.doGet(request, response);

        verify(request).setAttribute(eq("cartItemCount"), eq(3));
        verify(dispatcher).forward(request, response);
    }
}
