package org.example.kafka;

import org.example.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    public static final String TOPIC_PAYMENT_COMPLETED = "payment.completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(PaymentCompletedEvent event) {
        kafkaTemplate.send(TOPIC_PAYMENT_COMPLETED, event.orderId().toString(), event);
    }
}
