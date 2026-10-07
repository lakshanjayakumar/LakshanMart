package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.CartItem;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for user shopping cart operations.
 */
public interface CartDAO {

    /**
     * Finds all cart items for a given user, joining product details.
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Finds a specific user's cart item for a product.
     */
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId);

    /**
     * Adds an item to the user's cart.
     */
    CartItem create(CartItem item);

    /**
     * Updates the quantity of a cart item with user ownership verification.
     */
    boolean updateQuantity(Long cartItemId, Long userId, int quantity);

    /**
     * Deletes an item from the cart with user ownership verification.
     */
    boolean delete(Long cartItemId, Long userId);

    /**
     * Removes all items from a user's cart (called upon checkout).
     */
    void clearCart(Long userId);
}
