package org.example.kafka;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.event.PaymentCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String TOPIC = "payment.completed";

    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

    public void publish(PaymentCompletedEvent event) {
        kafkaTemplate.send(TOPIC, event.orderId().toString(), event);
    }
}

