package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.CartDAO;
import com.lakshan.lakshanmart.dao.OrderDAO;
import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dto.CheckoutRequest;
import com.lakshan.lakshanmart.dto.OrderResponseDTO;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.CartItem;
import com.lakshan.lakshanmart.model.Order;
import com.lakshan.lakshanmart.model.OrderStatus;
import com.lakshan.lakshanmart.model.Product;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private CartDAO cartDAO;

    @Mock
    private ProductDAO productDAO;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderDAO, cartDAO, productDAO);
    }

    @Test
    @DisplayName("Checkout fails when cart is empty")
    void testCheckoutEmptyCart() {
        when(cartDAO.findByUserId(1L)).thenReturn(Collections.emptyList());

        CheckoutRequest req = new CheckoutRequest("123 Street, City", "UPI");
        assertThrows(ValidationException.class, () -> orderService.checkout(1L, req));
    }

    @Test
    @DisplayName("Checkout succeeds with valid cart and stock")
    void testCheckoutSuccess() {
        CartItem item = new CartItem(1L, 1L, 10L, 2);
        when(cartDAO.findByUserId(1L)).thenReturn(List.of(item));

        Product product = new Product(10L, 2L, "Monitor", "4K",
                new BigDecimal("299.99"), 10, "Electronics", null, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(product));

        when(orderDAO.createOrder(any(Order.class), anyList())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(500L);
            o.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return o;
        });

        CheckoutRequest req = new CheckoutRequest("123 Tech Park, Chennai", "CARD");
        OrderResponseDTO result = orderService.checkout(1L, req);

        assertNotNull(result);
        assertEquals(500L, result.getId());
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals(new BigDecimal("599.98"), result.getTotalAmount());
        verify(orderDAO).createOrder(any(Order.class), anyList());
    }

    @Test
    @DisplayName("Update order status succeeds for valid status")
    void testUpdateStatusSuccess() {
        Order existing = new Order();
        existing.setId(500L);
        existing.setStatus(OrderStatus.CONFIRMED);
        existing.setTotalAmount(new BigDecimal("100.00"));

        when(orderDAO.findById(500L)).thenReturn(Optional.of(existing));
        when(orderDAO.updateStatus(500L, "SHIPPED")).thenReturn(true);

        OrderResponseDTO updated = orderService.updateOrderStatus(500L, "SHIPPED");
        assertNotNull(updated);
        assertEquals("SHIPPED", updated.getStatus());
        verify(orderDAO).updateStatus(500L, "SHIPPED");
    }

    @Test
    @DisplayName("Update order status fails with invalid status name")
    void testUpdateStatusInvalid() {
        assertThrows(ValidationException.class, () ->
                orderService.updateOrderStatus(500L, "INVALID_STATUS"));
    }
}
