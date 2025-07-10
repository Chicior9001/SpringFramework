package com.example.BookStore.service;

import com.example.BookStore.model.Cart;
import com.example.BookStore.model.User;

public interface CartService {
    Cart getCartForUser(User user);
    Cart addToCart(User user, Long bookId, int quantity);
    boolean removeFromCart(Long itemId);
    boolean clearCart(User user);
}
