package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.CartDAO;
import com.lakshan.lakshanmart.dao.ProductDAO;
import com.lakshan.lakshanmart.dto.AddToCartRequest;
import com.lakshan.lakshanmart.dto.CartItemDTO;
import com.lakshan.lakshanmart.dto.CartResponseDTO;
import com.lakshan.lakshanmart.exception.ResourceNotFoundException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.CartItem;
import com.lakshan.lakshanmart.model.Product;
import com.lakshan.lakshanmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CartService with stock verification and cart calculations.
 */
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public CartResponseDTO getCart(Long userId) {
        ValidationUtil.requirePositiveId(userId, "User");
        List<CartItem> items = cartDAO.findByUserId(userId);
        return buildCartResponse(items);
    }

    @Override
    public CartResponseDTO addToCart(Long userId, AddToCartRequest request) {
        ValidationUtil.requirePositiveId(userId, "User");
        if (request == null) {
            throw new ValidationException("Add to cart payload cannot be null.");
        }
        ValidationUtil.requirePositiveId(request.getProductId(), "Product");
        int qtyToAdd = (request.getQuantity() != null && request.getQuantity() > 0) ? request.getQuantity() : 1;

        Product product = productDAO.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        if (product.getStockQty() <= 0) {
            throw new ValidationException("Product '" + product.getName() + "' is out of stock.");
        }

        Optional<CartItem> existingItemOpt = cartDAO.findByUserAndProduct(userId, request.getProductId());
        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            int newQuantity = existing.getQuantity() + qtyToAdd;
            if (newQuantity > product.getStockQty()) {
                throw new ValidationException("Cannot add " + qtyToAdd + " more items. Only " + product.getStockQty() + " available in stock.");
            }
            cartDAO.updateQuantity(existing.getId(), userId, newQuantity);
        } else {
            if (qtyToAdd > product.getStockQty()) {
                throw new ValidationException("Requested quantity (" + qtyToAdd + ") exceeds available stock (" + product.getStockQty() + ").");
            }
            CartItem newItem = new CartItem(null, userId, request.getProductId(), qtyToAdd, null);
            cartDAO.create(newItem);
        }

        return getCart(userId);
    }

    @Override
    public CartResponseDTO updateCartItem(Long userId, Long cartItemId, int quantity) {
        ValidationUtil.requirePositiveId(userId, "User");
        ValidationUtil.requirePositiveId(cartItemId, "Cart Item");

        if (quantity <= 0) {
            cartDAO.delete(cartItemId, userId);
            return getCart(userId);
        }

        List<CartItem> items = cartDAO.findByUserId(userId);
        CartItem target = items.stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        if (quantity > target.getProductStock()) {
            throw new ValidationException("Requested quantity (" + quantity + ") exceeds available stock (" + target.getProductStock() + ").");
        }

        cartDAO.updateQuantity(cartItemId, userId, quantity);
        return getCart(userId);
    }

    @Override
    public CartResponseDTO removeCartItem(Long userId, Long cartItemId) {
        ValidationUtil.requirePositiveId(userId, "User");
        ValidationUtil.requirePositiveId(cartItemId, "Cart Item");

        boolean deleted = cartDAO.delete(cartItemId, userId);
        if (!deleted) {
            logger.warn("Cart item {} for user {} was not found for removal", cartItemId, userId);
        }
        return getCart(userId);
    }

    @Override
    public void clearCart(Long userId) {
        ValidationUtil.requirePositiveId(userId, "User");
        cartDAO.clearCart(userId);
    }

    private CartResponseDTO buildCartResponse(List<CartItem> items) {
        List<CartItemDTO> dtoList = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {
            BigDecimal subtotal = item.getSubtotal();
            total = total.add(subtotal);
            totalItems += item.getQuantity();

            dtoList.add(new CartItemDTO(
                    item.getId(),
                    item.getProductId(),
                    item.getProductName(),
                    item.getProductImageUrl(),
                    item.getProductPrice(),
                    item.getQuantity(),
                    subtotal,
                    item.getProductStock()
            ));
        }

        return new CartResponseDTO(dtoList, total, totalItems);
    }
}
