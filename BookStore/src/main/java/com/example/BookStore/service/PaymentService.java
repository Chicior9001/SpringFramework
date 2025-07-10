package com.example.BookStore.service;

import com.example.BookStore.model.Order;
import com.example.BookStore.model.User;

public interface PaymentService {
    String createCheckoutSession(Order order);
    void handleWebhook(String payload, String signature);
}