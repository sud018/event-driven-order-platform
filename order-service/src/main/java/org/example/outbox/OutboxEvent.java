package org.example.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events",
        indexes = @Index(name = "idx_outbox_status_created", columnList = "status,createdAt"))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OutboxEvent {

    @Id
    private UUID id;

    private String aggregateType;   // "ORDER"
    private UUID aggregateId;       // orderId
    private String eventType;       // "OrderCreatedEvent"
    private String topic;           // "order.created"

    @Column(columnDefinition = "TEXT")
    private String payload;         // JSON string

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Instant createdAt;
    private Instant sentAt;

    private int attempts;
}
