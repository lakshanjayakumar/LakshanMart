package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for product catalog persistence.
 */
public interface ProductDAO {

    /**
     * Finds a single product by primary key ID.
     */
    Optional<Product> findById(Long id);

    /**
     * Lists products with pagination.
     */
    List<Product> findAll(int offset, int limit);

    /**
     * Lists products filtered by category with pagination.
     */
    List<Product> findByCategory(String category, int offset, int limit);

    /**
     * Searches products by keyword matching in name or description, optionally filtered by category.
     */
    List<Product> search(String keyword, String category, int offset, int limit);

    /**
     * Inserts a new product listing.
     */
    Product create(Product product);

    /**
     * Updates an existing product listing.
     */
    boolean update(Product product);

    /**
     * Deletes a product listing by primary key ID.
     */
    boolean delete(Long id);

    /**
     * Finds products listed by a specific seller.
     */
    List<Product> findBySellerId(Long sellerId, int offset, int limit);
}
