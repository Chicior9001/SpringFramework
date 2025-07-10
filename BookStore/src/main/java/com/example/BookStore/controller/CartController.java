package com.example.BookStore.controller;

import com.example.BookStore.model.Cart;
import com.example.BookStore.model.User;
import com.example.BookStore.service.CartService;
import com.example.BookStore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Cart> viewCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername()).orElseThrow();
        Cart cart = cartService.getCartForUser(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long bookId, @RequestParam int quantity) {
        User user = userService.findByLogin(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(cartService.addToCart(user, bookId, quantity));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long itemId) {
        if(cartService.removeFromCart(itemId)) {
            return ResponseEntity.ok("Item deleted");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername()).orElseThrow();
        if(cartService.clearCart(user)) {
            return ResponseEntity.ok("Cart cleared");
        }
        return ResponseEntity.ok("Cart already empty");
    }
}