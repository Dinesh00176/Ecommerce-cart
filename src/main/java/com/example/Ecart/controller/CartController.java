package com.example.Ecart.controller;

import com.example.Ecart.model.Cart;
import com.example.Ecart.model.CartItem;
import com.example.Ecart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ✅ Add or update entire cart
    @PostMapping
    public ResponseEntity<Cart> updateCart(@RequestBody Cart cart) {
        Cart updatedCart = cartService.updateCart(cart);
        return ResponseEntity.ok(updatedCart);
    }

    // ✅ Get cart by user ID
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable String userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart != null) {
            return ResponseEntity.ok(cart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Clear entire cart
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Update a single item in the cart
    @PutMapping("/{userId}/{productId}")
    public ResponseEntity<Cart> updateCartItem(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestBody CartItem updatedItem) {
        Cart updatedCart = cartService.updateCartItem(userId, productId, updatedItem);
        return ResponseEntity.ok(updatedCart);
    }

    // ✅ Delete a single item from the cart
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable String userId,
            @PathVariable String productId) {
        cartService.deleteCartItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
