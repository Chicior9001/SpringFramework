package com.example.BookStore.controller;

import com.example.BookStore.model.Order;
import com.example.BookStore.model.OrderStatus;
import com.example.BookStore.repository.OrderRepository;
import com.example.BookStore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;


    @PostMapping("/checkout/{id}")
    public ResponseEntity<String> createCheckoutSession(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID: " + id + " doesn't exist"));

        if(order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order with ID: " + id + " is already processed");
        }

        String url = paymentService.createCheckoutSession(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    public ResponseEntity<String> success() {
        return ResponseEntity.ok("Payment successful!");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancel() {
        return ResponseEntity.ok("Payment canceled!");
    }
}