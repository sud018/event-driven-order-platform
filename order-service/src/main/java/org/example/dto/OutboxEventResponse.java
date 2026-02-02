package org.example.dto;

import org.example.outbox.OutboxStatus;
import java.time.Instant;
import java.util.UUID;

public record OutboxEventResponse(
        UUID id,
        String aggregateType,
        UUID aggregateId,
        String eventType,
        String topic,
        OutboxStatus status,
        int attempts,
        Instant createdAt,
        Instant sentAt,
        int payloadLength,
        String payloadPreview
) {}
