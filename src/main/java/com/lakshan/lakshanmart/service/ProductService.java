package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dto.ProductCreateRequest;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.ProductUpdateRequest;

import java.util.List;

/**
 * Service interface for product catalog and admin/seller product management.
 */
public interface ProductService {

    /**
     * Retrieves product details by ID.
     */
    ProductResponseDTO getProductById(Long id);

    /**
     * Lists products with optional category filtering and pagination.
     */
    List<ProductResponseDTO> listProducts(String category, int offset, int limit);

    /**
     * Searches products by keyword matching in title or description.
     */
    List<ProductResponseDTO> searchProducts(String keyword, String category, int offset, int limit);

    /**
     * Creates a new product listing for a seller or admin.
     */
    ProductResponseDTO createProduct(Long sellerId, ProductCreateRequest request);

    /**
     * Updates an existing product listing with authorization check.
     */
    ProductResponseDTO updateProduct(Long requesterId, String requesterRole, ProductUpdateRequest request);

    /**
     * Deletes a product listing with authorization check.
     */
    void deleteProduct(Long requesterId, String requesterRole, Long productId);

    /**
     * Lists products belonging to a specific seller.
     */
    List<ProductResponseDTO> getProductsBySeller(Long sellerId, int offset, int limit);
}
