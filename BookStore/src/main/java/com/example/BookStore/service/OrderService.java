package com.example.BookStore.service;

import com.example.BookStore.model.Order;
import com.example.BookStore.model.OrderStatus;
import com.example.BookStore.model.User;

import java.util.List;

public interface OrderService {
    Order placeOrder(User user);
    List<Order> getOrdersForUser(User user);
    List<Order> getAllOrders();
    void updateOrderStatus(Long orderId, OrderStatus status);
}
