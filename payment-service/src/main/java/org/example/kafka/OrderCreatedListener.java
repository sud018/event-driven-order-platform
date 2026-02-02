package org.example.kafka;

import org.example.event.OrderCreatedEvent;
import org.example.event.PaymentCompletedEvent;
import org.example.model.Payment;
import org.example.model.PaymentStatus;
import org.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer producer;

    @KafkaListener(topics = "order.created")
    public void onMessage(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);
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
        catch (Exception e) {
            throw new RuntimeException("Failed to parse OrderCreatedEvent payload: " + payload, e);
        }
    }
}

