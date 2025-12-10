package com.example.Ecart.service;

import com.example.Ecart.model.Cart;
import com.example.Ecart.model.CartItem;
import com.example.Ecart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Cart updateCart(Cart cart) {
        Cart existingCart = cartRepository.findByUserId(cart.getUserId()).orElse(null);

        if (existingCart != null) {
            if (cart.getItems() != null) {
                // Remove items in existingCart that are NOT in the new list
                existingCart.getItems().removeIf(existingItem -> 
                    cart.getItems().stream()
                        .noneMatch(newItem -> newItem.getProductId().equals(existingItem.getProductId()))
                );

                // Update existing items or add new ones
                for (CartItem newItem : cart.getItems()) {
                    Optional<CartItem> existingItemOpt = existingCart.getItems().stream()
                        .filter(item -> item.getProductId().equals(newItem.getProductId()))
                        .findFirst();

                    if (existingItemOpt.isPresent()) {
                        CartItem existingItem = existingItemOpt.get();
                        existingItem.setProductName(newItem.getProductName());
                        existingItem.setQuantity(newItem.getQuantity());
                        existingItem.setPrice(newItem.getPrice());
                    } else {
                        newItem.setCart(existingCart);
                        existingCart.getItems().add(newItem);
                    }
                }
            }
            return cartRepository.save(existingCart);
        } else {
            if (cart.getItems() != null) {
                cart.getItems().forEach(item -> item.setCart(cart));
            }
            return cartRepository.save(cart);
        }
    }

    // Get cart by user ID
    public Cart getCartByUserId(String userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        return cartOpt.orElse(null);
    }

    // Clear entire cart items, keep the cart
    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        
        cart.getItems().clear();  // Clear all cart items
        
        cartRepository.save(cart); // Save cart with empty items list
    }

    // Update a specific item in the cart
    public Cart updateCartItem(String userId, String productId, CartItem updatedItem) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for user: " + userId);
        }

        boolean found = false;

        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(updatedItem.getQuantity());
                item.setPrice(updatedItem.getPrice());
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Product not found in cart: " + productId);
        }

        return cartRepository.save(cart);
    }

    // Delete a specific item from the cart
    public void deleteCartItem(String userId, String productId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for user: " + userId);
        }

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        cartRepository.save(cart); // Save updated cart without that item
    }
}
