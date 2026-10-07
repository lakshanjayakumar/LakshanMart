package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.Product;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOTest extends BaseDAOTest {

    private ProductDAO productDAO;
    private UserDAO userDAO;
    private Long sellerId;

    @BeforeEach
    void setUp() {
        productDAO = new ProductDAOImpl();
        userDAO = new UserDAOImpl();

        // Create a seller user for FK constraint
        User seller = new User(null, "Test Merchant", "merchant@test.com", "hash", Role.SELLER, null);
        User createdSeller = userDAO.create(seller);
        sellerId = createdSeller.getId();
    }

    @Test
    @DisplayName("Create product and retrieve by ID")
    void testCreateAndFindById() {
        Product p = new Product(null, sellerId, "Ergonomic Chair", "Comfortable desk chair",
                new BigDecimal("199.99"), 15, "Furniture", "https://img.url/chair.jpg", null);

        Product created = productDAO.create(p);
        assertNotNull(created.getId());

        Optional<Product> found = productDAO.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Ergonomic Chair", found.get().getName());
        assertEquals(0, new BigDecimal("199.99").compareTo(found.get().getPrice()));
        assertEquals(15, found.get().getStockQty());
    }

    @Test
    @DisplayName("Filter products by category")
    void testFindByCategory() {
        productDAO.create(new Product(null, sellerId, "Laptop", "Fast laptop", new BigDecimal("999.00"), 5, "Electronics", null, null));
        productDAO.create(new Product(null, sellerId, "Phone", "Smartphone", new BigDecimal("499.00"), 10, "Electronics", null, null));
        productDAO.create(new Product(null, sellerId, "Coffee Mug", "Ceramic mug", new BigDecimal("12.00"), 50, "Kitchen", null, null));

        List<Product> electronics = productDAO.findByCategory("Electronics", 0, 10);
        assertEquals(2, electronics.size());

        List<Product> kitchen = productDAO.findByCategory("Kitchen", 0, 10);
        assertEquals(1, kitchen.size());
    }

    @Test
    @DisplayName("Search products by keyword")
    void testSearchProducts() {
        productDAO.create(new Product(null, sellerId, "Apple iPhone 15", "Latest model", new BigDecimal("799.00"), 8, "Smartphones", null, null));
        productDAO.create(new Product(null, sellerId, "Samsung Galaxy", "Android phone", new BigDecimal("699.00"), 12, "Smartphones", null, null));

        List<Product> results = productDAO.search("apple", null, 0, 10);
        assertEquals(1, results.size());
        assertEquals("Apple iPhone 15", results.get(0).getName());

        List<Product> phoneResults = productDAO.search("phone", null, 0, 10);
        assertEquals(2, phoneResults.size());
    }

    @Test
    @DisplayName("Update existing product")
    void testUpdateProduct() {
        Product p = productDAO.create(new Product(null, sellerId, "Original Name", "Desc",
                new BigDecimal("50.00"), 10, "Category1", null, null));

        p.setName("Updated Name");
        p.setPrice(new BigDecimal("75.00"));
        p.setStockQty(20);

        boolean updated = productDAO.update(p);
        assertTrue(updated);

        Product fetched = productDAO.findById(p.getId()).orElseThrow();
        assertEquals("Updated Name", fetched.getName());
        assertEquals(0, new BigDecimal("75.00").compareTo(fetched.getPrice()));
        assertEquals(20, fetched.getStockQty());
    }

    @Test
    @DisplayName("Delete product by ID")
    void testDeleteProduct() {
        Product p = productDAO.create(new Product(null, sellerId, "To Delete", "Desc",
                new BigDecimal("10.00"), 2, "General", null, null));

        boolean deleted = productDAO.delete(p.getId());
        assertTrue(deleted);

        Optional<Product> fetched = productDAO.findById(p.getId());
        assertTrue(fetched.isEmpty());
    }
}
