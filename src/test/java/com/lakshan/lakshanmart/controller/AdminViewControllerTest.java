package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.service.OrderService;
import com.lakshan.lakshanmart.service.ProductService;
import com.lakshan.lakshanmart.service.UserService;
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
class AdminViewControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private AdminViewController adminViewController;

    @BeforeEach
    void setUp() {
        adminViewController = new AdminViewController(productService, orderService, userService);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(request.getContextPath()).thenReturn("/LakshanMart");
        when(request.getServletPath()).thenReturn("/admin");
    }

    @Test
    @DisplayName("Unauthenticated request to /admin redirects to /login")
    void testAdminUnauthenticatedRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        adminViewController.doGet(request, response);

        verify(response).sendRedirect("/LakshanMart/login?redirect=/LakshanMart/admin");
    }

    @Test
    @DisplayName("Buyer access to /admin is rejected with 403 Forbidden")
    void testAdminForbiddenForBuyer() throws Exception {
        UserResponseDTO buyer = new UserResponseDTO(2L, "Buyer", "buyer@example.com", "BUYER", new Timestamp(System.currentTimeMillis()));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(AuthFilter.SESSION_USER_KEY)).thenReturn(buyer);

        adminViewController.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
    }

    @Test
    @DisplayName("Admin access to /admin succeeds and forwards to dashboard.jsp with KPI metrics")
    void testAdminDashboardSuccess() throws Exception {
        UserResponseDTO admin = new UserResponseDTO(1L, "Admin", "admin@example.com", "ADMIN", new Timestamp(System.currentTimeMillis()));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(AuthFilter.SESSION_USER_KEY)).thenReturn(admin);

        when(productService.listProducts(null, 0, 500)).thenReturn(List.of(
                new ProductResponseDTO(1L, 1L, "Prod 1", "Desc", new BigDecimal("50.00"), 10, "Electronics", null, null)
        ));
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());
        when(userService.getAllUsers()).thenReturn(List.of(admin));

        adminViewController.doGet(request, response);

        verify(request).setAttribute(eq("totalProducts"), eq(1));
        verify(request).setAttribute(eq("totalOrders"), eq(0));
        verify(request).setAttribute(eq("totalUsers"), eq(1));
        verify(request).getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp");
        verify(dispatcher).forward(request, response);
    }
}
