package org.example.kafka;

import org.example.event.OrderCreatedEvent;
import org.example.model.Payment;
import org.example.model.PaymentStatus;
import org.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "order.created", groupId = "payment-service-group")
    public void handle(OrderCreatedEvent event) {
        System.out.println("Received order event: " + event);

        Payment payment = Payment.builder()
                .orderId(event.orderId())
                .amount(event.amount())
                .status(PaymentStatus.SUCCESS)
                .build();

        paymentRepository.save(payment);
    }
}
