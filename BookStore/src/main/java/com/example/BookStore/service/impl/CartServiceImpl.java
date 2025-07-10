package com.example.BookStore.service.impl;

import com.example.BookStore.model.Book;
import com.example.BookStore.model.Cart;
import com.example.BookStore.model.CartItem;
import com.example.BookStore.model.User;
import com.example.BookStore.repository.BookRepository;
import com.example.BookStore.repository.CartItemRepository;
import com.example.BookStore.repository.CartRepository;
import com.example.BookStore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public Cart getCartForUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart addToCart(User user, Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Cart cart = getCartForUser(user);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setBook(book);
        item.setQuantity(quantity);

        cart.getItems().add(item);
        cartItemRepository.save(item);
        return cartRepository.save(cart);
    }

    @Override
    public boolean removeFromCart(Long itemId) {
        if(!cartItemRepository.existsById(itemId)) {
            return false;
        }
        cartItemRepository.deleteById(itemId);
        return true;
    }

    @Override
    public boolean clearCart(User user) {
        Cart cart = getCartForUser(user);
        if(cart.getItems().isEmpty()) {
            return false;
        }
        cart.getItems().clear();
        cartRepository.save(cart);
        return true;
    }
}