package com.example.BookStore.service.impl;

import com.example.BookStore.model.*;
import com.example.BookStore.repository.OrderRepository;
import com.example.BookStore.repository.PaymentRepository;
import com.example.BookStore.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${STRIPE_API_KEY}")
    private String apiKey;
    @Value("${WEBHOOK_SECRET}")
    private String webhookSecret;

    @Override
    @Transactional
    public String createCheckoutSession(Order order) {
        Stripe.apiKey = apiKey;

        double totalAmount = 0;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(item.getBook().getTitle())
                            .build();

            var amount = item.getBook().getPrice() * 100;
            totalAmount += amount;

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("pln")
                    .setUnitAmount((long) amount)
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPriceData(priceData)
                            .build();

            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/payments/success")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .build();

        try {
            Session session = Session.create(params);
            Payment payment = Payment.builder()
                    .order(order)
                    .user(order.getUser())
                    .amount(totalAmount)
                    .stripeSessionId(session.getId())
                    .status(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        Stripe.apiKey = apiKey;
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature", e);
        }

        if("checkout.session.completed".equals(event.getType())) {
            StripeObject stripeObject =
                    event.getDataObjectDeserializer().getObject().orElseThrow();
            String sessionId = ((Session)stripeObject).getId();

            if(sessionId != null) {
                paymentRepository.findByStripeSessionId(sessionId).ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                    Order order = payment.getOrder();
                    System.out.println("Processing order " + order.getId());
                    order.setStatus(OrderStatus.PROCESSING);
                    orderRepository.save(order);
                    System.out.println("Order is ready for shipping " + order.getId());
                });
            }
        }
    }
}
