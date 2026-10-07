package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.CartResponseDTO;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.service.CartService;
import com.lakshan.lakshanmart.service.OrderService;
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
class ProfileViewControllerTest {

    @Mock
    private OrderService orderService;

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

    private ProfileViewController profileViewController;

    @BeforeEach
    void setUp() {
        profileViewController = new ProfileViewController(orderService, cartService);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(request.getContextPath()).thenReturn("/LakshanMart");
    }

    @Test
    @DisplayName("GET /profile redirects unauthenticated users to login")
    void testGetProfileUnauthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        profileViewController.doGet(request, response);

        verify(response).sendRedirect("/LakshanMart/login?redirect=/LakshanMart/profile");
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    @DisplayName("GET /profile forwards to profile.jsp for authenticated user")
    void testGetProfileAuthenticated() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Alice Buyer", "buyer@example.com", "BUYER", new Timestamp(System.currentTimeMillis()));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(AuthFilter.SESSION_USER_KEY)).thenReturn(user);

        List<OrderResponseDTO> orders = List.of(
                new OrderResponseDTO(101L, 1L, "Alice Buyer", "COMPLETED", new BigDecimal("150.00"), "123 Main St", new Timestamp(System.currentTimeMillis()), Collections.emptyList())
        );
        when(orderService.getUserOrders(1L)).thenReturn(orders);
        when(cartService.getCart(1L)).thenReturn(new CartResponseDTO(Collections.emptyList(), BigDecimal.ZERO, 2));

        profileViewController.doGet(request, response);

        verify(request).setAttribute(eq("user"), eq(user));
        verify(request).setAttribute(eq("orders"), eq(orders));
        verify(request).setAttribute(eq("totalOrders"), eq(1));
        verify(request).setAttribute(eq("cartItemCount"), eq(2));
        verify(request).getRequestDispatcher("/WEB-INF/views/auth/profile.jsp");
        verify(dispatcher).forward(request, response);
    }
}
