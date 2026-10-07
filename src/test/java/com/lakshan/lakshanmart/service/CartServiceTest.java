package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.CartDAO;
import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dto.AddToCartRequest;
import com.lakshan.lakshanmart.dto.CartResponseDTO;
import com.lakshan.lakshanmart.exception.ResourceNotFoundException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.CartItem;
import com.lakshan.lakshanmart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartDAO cartDAO;

    @Mock
    private ProductDAO productDAO;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartDAO, productDAO);
    }

    @Test
    @DisplayName("Add to cart succeeds when product exists and has stock")
    void testAddToCartSuccess() {
        Product p = new Product(10L, 2L, "Keyboard", "Mechanical",
                new BigDecimal("79.99"), 20, "Electronics", null, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(p));
        when(cartDAO.findByUserAndProduct(1L, 10L)).thenReturn(Optional.empty());

        CartItem created = new CartItem(100L, 1L, 10L, 2);
        when(cartDAO.create(any(CartItem.class))).thenReturn(created);

        when(cartDAO.findByUserId(1L)).thenReturn(List.of(created));

        CartResponseDTO cart = cartService.addToCart(1L, new AddToCartRequest(10L, 2));
        assertNotNull(cart);
        assertEquals(2, cart.getItemCount());
        verify(cartDAO).create(any(CartItem.class));
    }

    @Test
    @DisplayName("Add to cart rejects quantity exceeding available stock")
    void testAddToCartExceedsStock() {
        Product p = new Product(10L, 2L, "Keyboard", "Mechanical",
                new BigDecimal("79.99"), 3, "Electronics", null, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(p));

        AddToCartRequest req = new AddToCartRequest(10L, 5);
        assertThrows(ValidationException.class, () -> cartService.addToCart(1L, req));
    }

    @Test
    @DisplayName("Add to cart rejects non-existent product")
    void testAddToCartProductNotFound() {
        when(productDAO.findById(999L)).thenReturn(Optional.empty());

        AddToCartRequest req = new AddToCartRequest(999L, 1);
        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(1L, req));
    }

    @Test
    @DisplayName("Get cart returns empty list and zero totals for empty cart")
    void testGetEmptyCart() {
        when(cartDAO.findByUserId(1L)).thenReturn(Collections.emptyList());

        CartResponseDTO cart = cartService.getCart(1L);
        assertNotNull(cart);
        assertEquals(0, cart.getItemCount());
        assertEquals(BigDecimal.ZERO, cart.getTotalAmount());
        assertTrue(cart.getItems().isEmpty());
    }
}
