package org.example.kafka;

import org.example.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String TOPIC = "order.created";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.orderId().toString(), event);
    }
}