package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dto.ProductCreateRequest;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.ProductUpdateRequest;
import com.lakshan.lakshanmart.exception.ForbiddenException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productDAO);
    }

    @Test
    @DisplayName("Create product succeeds with valid input")
    void testCreateProductSuccess() {
        ProductCreateRequest req = new ProductCreateRequest("Wireless Mouse", "Ergonomic mouse",
                new BigDecimal("29.99"), 50, "Electronics", "http://img.png");

        when(productDAO.create(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(100L);
            p.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return p;
        });

        ProductResponseDTO created = productService.createProduct(2L, req);
        assertNotNull(created);
        assertEquals(100L, created.getId());
        assertEquals("Wireless Mouse", created.getName());
        assertEquals(2L, created.getSellerId());
    }

    @Test
    @DisplayName("Create product rejects negative price or stock")
    void testCreateProductInvalidPrice() {
        ProductCreateRequest req = new ProductCreateRequest("Invalid Item", "Desc",
                new BigDecimal("-5.00"), 10, "Category", null);

        assertThrows(ValidationException.class, () -> productService.createProduct(1L, req));
    }

    @Test
    @DisplayName("Seller cannot update another seller's product")
    void testUpdateProductForbiddenForNonOwner() {
        Product existing = new Product(50L, 2L, "Seller 2 Product", "Desc",
                new BigDecimal("20.00"), 5, "General", null, null);

        when(productDAO.findById(50L)).thenReturn(Optional.of(existing));

        ProductUpdateRequest req = new ProductUpdateRequest(50L, "Hacked Name", "Desc",
                new BigDecimal("20.00"), 5, "General", null);

        // User 3 (SELLER) attempts to modify User 2's product
        assertThrows(ForbiddenException.class, () ->
                productService.updateProduct(3L, "SELLER", req));

        verify(productDAO, never()).update(any());
    }

    @Test
    @DisplayName("Admin can update any seller's product")
    void testUpdateProductAdminCanModerate() {
        Product existing = new Product(50L, 2L, "Seller 2 Product", "Desc",
                new BigDecimal("20.00"), 5, "General", null, null);

        when(productDAO.findById(50L)).thenReturn(Optional.of(existing));
        when(productDAO.update(any(Product.class))).thenReturn(true);

        ProductUpdateRequest req = new ProductUpdateRequest(50L, "Moderated Name", "Desc",
                new BigDecimal("20.00"), 5, "General", null);

        // Admin modifies User 2's product
        ProductResponseDTO updated = productService.updateProduct(1L, "ADMIN", req);
        assertNotNull(updated);
        assertEquals("Moderated Name", updated.getName());
        verify(productDAO).update(any(Product.class));
    }
}
