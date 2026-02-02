package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.event.PaymentCompletedEvent;
import org.example.model.OrderStatus;
import org.example.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment.completed", groupId = "order-service-group")
    public void onMessage(String payload) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(payload, PaymentCompletedEvent.class);

            orderRepository.findById(event.orderId()).ifPresent(order -> {
                if ("SUCCESS".equalsIgnoreCase(event.status())) {
                    order.setStatus(OrderStatus.PAID);
                } else {
                    order.setStatus(OrderStatus.FAILED);
                }
                orderRepository.save(order);
                System.out.println("Order updated after payment: " + order.getId() + " -> " + order.getStatus());
            });

        } catch (Exception e) {
            // If parsing fails, we should not crash the app.
            // Later we can route this to a DLT similar to order.created
            throw new RuntimeException("Failed to parse PaymentCompletedEvent payload: " + payload, e);
        }
    }
}
