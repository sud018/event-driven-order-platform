package org.example.kafka;

import org.example.event.OrderCreatedEvent;
import org.example.event.PaymentCompletedEvent;
import org.example.model.Payment;
import org.example.model.PaymentStatus;
import org.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer producer;

    @KafkaListener(topics = "order.created", groupId = "payment-service-group")
    public void onMessage(OrderCreatedEvent event) {
        if (paymentRepository.existsByOrderId(event.orderId())) {
            System.out.println("Duplicate order event ignored: " + event.orderId());
            return;
        }

        System.out.println("Payment Service received: " + event);

        Payment payment = Payment.builder()
                .orderId(event.orderId())
                .amount(event.amount())
                .status(PaymentStatus.SUCCESS)
                .build();

        Payment saved = paymentRepository.save(payment);

        PaymentCompletedEvent out = new PaymentCompletedEvent(
                UUID.randomUUID(),
                saved.getId(),
                saved.getOrderId(),
                saved.getAmount(),
                saved.getStatus().name(),
                Instant.now()
        );

        producer.publish(out);
    }
}

