package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dto.AddToCartRequest;
import com.lakshan.lakshanmart.dto.CartResponseDTO;

/**
 * Service interface for shopping cart business logic.
 */
public interface CartService {

    /**
     * Retrieves the current shopping cart and calculated totals for a user.
     */
    CartResponseDTO getCart(Long userId);

    /**
     * Adds an item to the shopping cart, validating product existence and stock.
     */
    CartResponseDTO addToCart(Long userId, AddToCartRequest request);

    /**
     * Updates an existing cart item's quantity.
     */
    CartResponseDTO updateCartItem(Long userId, Long cartItemId, int quantity);

    /**
     * Removes an item from the shopping cart.
     */
    CartResponseDTO removeCartItem(Long userId, Long cartItemId);

    /**
     * Clears all items in the shopping cart.
     */
    void clearCart(Long userId);
}
