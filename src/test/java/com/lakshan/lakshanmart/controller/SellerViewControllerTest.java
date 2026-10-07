package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.ProductCreateRequest;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.SellerOrderItemDTO;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.service.OrderService;
import com.lakshan.lakshanmart.service.ProductService;
import com.lakshan.lakshanmart.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerViewControllerTest {

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
    private RequestDispatcher requestDispatcher;

    private SellerViewController controller;

    @BeforeEach
    void setUp() {
        controller = new SellerViewController(productService, orderService, userService);
    }

    @Test
    @DisplayName("Unauthenticated request redirects to login")
    void testUnauthenticatedRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/LakshanMart");
        when(request.getServletPath()).thenReturn("/seller");

        controller.doGet(request, response);

        verify(response).sendRedirect(contains("/login"));
    }

    @Test
    @DisplayName("Buyer accessing /seller/onboard gets forwarded to onboard.jsp")
    void testBuyerOnboardView() throws Exception {
        UserResponseDTO buyer = new UserResponseDTO(5L, "Buyer Bob", "bob@test.com", "BUYER", null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(buyer);
        when(request.getServletPath()).thenReturn("/seller/onboard");
        when(request.getRequestDispatcher("/WEB-INF/views/seller/onboard.jsp")).thenReturn(requestDispatcher);

        controller.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Seller accessing /seller/dashboard loads products and metrics")
    void testSellerDashboardView() throws Exception {
        UserResponseDTO seller = new UserResponseDTO(3L, "Merchant Alice", "alice@test.com", "SELLER", null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(seller);
        when(request.getServletPath()).thenReturn("/seller/dashboard");

        List<ProductResponseDTO> products = List.of(
                new ProductResponseDTO(1L, 3L, "Laptop", "Fast", new BigDecimal("50000.00"), 5, "Electronics", null, null)
        );
        List<SellerOrderItemDTO> soldItems = List.of(
                new SellerOrderItemDTO(100L, new Timestamp(System.currentTimeMillis()), "DELIVERED", "Bob Buyer",
                        1L, "Laptop", null, 2, new BigDecimal("50000.00"), new BigDecimal("100000.00"))
        );

        when(productService.getProductsBySeller(3L, 0, 100)).thenReturn(products);
        when(orderService.getSellerOrderItems(3L)).thenReturn(soldItems);
        when(request.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp")).thenReturn(requestDispatcher);

        controller.doGet(request, response);

        verify(request).setAttribute(eq("products"), eq(products));
        verify(request).setAttribute(eq("totalProducts"), eq(1));
        verify(request).setAttribute(eq("totalUnitsSold"), eq(2));
        verify(request).setAttribute(eq("totalEarnings"), eq(new BigDecimal("100000.00")));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST /seller/onboard updates user role to SELLER")
    void testOnboardSubmit() throws Exception {
        UserResponseDTO buyer = new UserResponseDTO(5L, "Buyer Bob", "bob@test.com", "BUYER", null);
        UserResponseDTO updated = new UserResponseDTO(5L, "Buyer Bob", "bob@test.com", "SELLER", null);

        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(buyer);
        when(request.getServletPath()).thenReturn("/seller/onboard");
        when(request.getContextPath()).thenReturn("/LakshanMart");
        when(userService.updateUserRole(5L, Role.SELLER.name())).thenReturn(updated);

        controller.doPost(request, response);

        verify(userService).updateUserRole(5L, "SELLER");
        verify(session).setAttribute("currentUser", updated);
        verify(response).sendRedirect(contains("/seller/dashboard?onboarded=true"));
    }

    @Test
    @DisplayName("POST /seller/product/create creates product listing")
    void testProductCreate() throws Exception {
        UserResponseDTO seller = new UserResponseDTO(3L, "Merchant Alice", "alice@test.com", "SELLER", null);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(seller);
        when(request.getServletPath()).thenReturn("/seller/product/create");
        when(request.getContextPath()).thenReturn("/LakshanMart");

        when(request.getParameter("name")).thenReturn("New Phone");
        when(request.getParameter("description")).thenReturn("Latest Model");
        when(request.getParameter("category")).thenReturn("Electronics");
        when(request.getParameter("price")).thenReturn("24999.00");
        when(request.getParameter("stockQty")).thenReturn("15");
        when(request.getParameter("imageUrl")).thenReturn("https://img.url/phone.jpg");

        controller.doPost(request, response);

        verify(productService).createProduct(eq(3L), any(ProductCreateRequest.class));
        verify(response).sendRedirect(contains("/seller/dashboard?success="));
    }
}
