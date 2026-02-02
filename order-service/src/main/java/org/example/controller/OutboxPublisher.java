package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.outbox.OutboxEvent;
import org.example.outbox.OutboxStatus;
import org.example.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPending() {

        var pending = outboxRepo.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent e : pending) {
            try {
                // publish JSON payload
                kafkaTemplate.send(e.getTopic(), e.getAggregateId().toString(), e.getPayload()).get();

                // update ONLY status/sentAt (no entity mutation)
                outboxRepo.markSent(e.getId(), OutboxStatus.SENT, Instant.now());

            } catch (Exception ex) {
                int nextAttempts = e.getAttempts() + 1;
                OutboxStatus nextStatus = (nextAttempts >= 10) ? OutboxStatus.FAILED : OutboxStatus.PENDING;

                outboxRepo.markFailedOrRetry(e.getId(), nextAttempts, nextStatus);
            }
        }
    }
}
