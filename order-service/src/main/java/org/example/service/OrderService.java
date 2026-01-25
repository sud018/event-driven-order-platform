package org.example.service;

import org.example.event.OrderCreatedEvent;
import org.example.kafka.OrderEventProducer;
import org.example.model.Order;
import org.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer producer;

    public Order createOrder(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }

        Order order = Order.builder()
                .amount(amount)
                .build();

        Order saved = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                saved.getId(),
                saved.getAmount(),
                Instant.now()
        );

        producer.publish(event);
        return saved;
    }
}
