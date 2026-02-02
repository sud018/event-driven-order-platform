package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.event.OrderCreatedEvent;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.outbox.OutboxEvent;
import org.example.repository.OutboxEventRepository;
import org.example.outbox.OutboxStatus;
import org.example.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public UUID createOrder(BigDecimal amount){

        Order order = new Order();
        order.setAmount(amount);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(Instant.now());

        Order saved = orderRepository.save(order);
        UUID orderId = saved.getId();

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                orderId,
                saved.getAmount(),
                Instant.now()
        );

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize OrderCreatedEvent", e);
        }

        OutboxEvent outbox = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateType("ORDER")
                .aggregateId(orderId)
                .eventType("OrderCreatedEvent")
                .topic("order.created")
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .attempts(0)
                .build();

        outboxRepo.save(outbox);

        return orderId;
    }


}
