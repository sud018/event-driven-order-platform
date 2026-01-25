package org.example.kafka;

import lombok.RequiredArgsConstructor;
import org.example.event.PaymentCompletedEvent;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedListener {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment.completed", groupId = "order-service-group")
    public void onMessage(PaymentCompletedEvent event) {
        orderRepository.findById(event.orderId()).ifPresent(order -> {
            if ("SUCCESS".equalsIgnoreCase(event.status())) {
                order.setStatus(OrderStatus.PAID);
            } else {
                order.setStatus(OrderStatus.FAILED);
            }
            orderRepository.save(order);
            System.out.println("Order updated after payment: " + order.getId() + " -> " + order.getStatus());
        });
    }
}
