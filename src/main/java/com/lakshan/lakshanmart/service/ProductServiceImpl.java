package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dto.ProductCreateRequest;
import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.dto.ProductUpdateRequest;
import com.lakshan.lakshanmart.exception.ForbiddenException;
import com.lakshan.lakshanmart.exception.ResourceNotFoundException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.Product;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService for product catalog browsing and management.
 */
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDAO productDAO;

    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        ValidationUtil.requirePositiveId(id, "Product");
        Product product = productDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return toResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> listProducts(String category, int offset, int limit) {
        List<Product> products;
        if (category != null && !category.trim().isEmpty()) {
            products = productDAO.findByCategory(category.trim(), offset, limit);
        } else {
            products = productDAO.findAll(offset, limit);
        }
        return products.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> searchProducts(String keyword, String category, int offset, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listProducts(category, offset, limit);
        }
        List<Product> products = productDAO.search(keyword.trim(), category, offset, limit);
        return products.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO createProduct(Long sellerId, ProductCreateRequest request) {
        ValidationUtil.requirePositiveId(sellerId, "Seller");
        if (request == null) {
            throw new ValidationException("Product creation payload cannot be null.");
        }
        ValidationUtil.requireNonBlank(request.getName(), "Product name");
        ValidationUtil.validatePositivePrice(request.getPrice());
        ValidationUtil.validateNonNegativeQuantity(request.getStockQty());
        ValidationUtil.requireNonBlank(request.getCategory(), "Category");

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        product.setPrice(request.getPrice());
        product.setStockQty(request.getStockQty());
        product.setCategory(request.getCategory().trim());
        product.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : null);

        Product created = productDAO.create(product);
        logger.info("Product created with ID: {} by seller: {}", created.getId(), sellerId);
        return toResponseDTO(created);
    }

    @Override
    public ProductResponseDTO updateProduct(Long requesterId, String requesterRole, ProductUpdateRequest request) {
        ValidationUtil.requirePositiveId(requesterId, "Requester");
        if (request == null) {
            throw new ValidationException("Product update payload cannot be null.");
        }
        ValidationUtil.requirePositiveId(request.getId(), "Product");
        ValidationUtil.requireNonBlank(request.getName(), "Product name");
        ValidationUtil.validatePositivePrice(request.getPrice());
        ValidationUtil.validateNonNegativeQuantity(request.getStockQty());
        ValidationUtil.requireNonBlank(request.getCategory(), "Category");

        Product existing = productDAO.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getId()));

        // Authorization check: Admin can moderate any listing; Seller can only modify their own listing
        boolean isAdmin = Role.ADMIN.name().equalsIgnoreCase(requesterRole);
        boolean isOwner = existing.getSellerId().equals(requesterId);

        if (!isAdmin && !isOwner) {
            logger.warn("Unauthorized attempt to update product {} by user {}", request.getId(), requesterId);
            throw new ForbiddenException("You are not authorized to modify this product listing.");
        }

        existing.setName(request.getName().trim());
        existing.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        existing.setPrice(request.getPrice());
        existing.setStockQty(request.getStockQty());
        existing.setCategory(request.getCategory().trim());
        existing.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : null);

        boolean updated = productDAO.update(existing);
        if (!updated) {
            throw new ResourceNotFoundException("Could not update product with ID: " + request.getId());
        }

        logger.info("Product {} updated successfully by user {}", existing.getId(), requesterId);
        return toResponseDTO(existing);
    }

    @Override
    public void deleteProduct(Long requesterId, String requesterRole, Long productId) {
        ValidationUtil.requirePositiveId(requesterId, "Requester");
        ValidationUtil.requirePositiveId(productId, "Product");

        Product existing = productDAO.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        boolean isAdmin = Role.ADMIN.name().equalsIgnoreCase(requesterRole);
        boolean isOwner = existing.getSellerId().equals(requesterId);

        if (!isAdmin && !isOwner) {
            logger.warn("Unauthorized attempt to delete product {} by user {}", productId, requesterId);
            throw new ForbiddenException("You are not authorized to delete this product listing.");
        }

        boolean deleted = productDAO.delete(productId);
        if (!deleted) {
            throw new ResourceNotFoundException("Product not found for deletion with ID: " + productId);
        }
        logger.info("Product {} deleted successfully by user {}", productId, requesterId);
    }

    @Override
    public List<ProductResponseDTO> getProductsBySeller(Long sellerId, int offset, int limit) {
        ValidationUtil.requirePositiveId(sellerId, "Seller");
        List<Product> products = productDAO.findBySellerId(sellerId, offset, limit);
        return products.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private ProductResponseDTO toResponseDTO(Product p) {
        return new ProductResponseDTO(
                p.getId(),
                p.getSellerId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQty(),
                p.getCategory(),
                p.getImageUrl(),
                p.getCreatedAt()
        );
    }
}
