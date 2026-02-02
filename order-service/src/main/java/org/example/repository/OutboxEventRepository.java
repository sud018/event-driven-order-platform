package org.example.repository;

import org.example.outbox.OutboxEvent;
import org.example.outbox.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusOrderByCreatedAtDesc(OutboxStatus status, Pageable pageable);

    List<OutboxEvent> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update OutboxEvent e
        set e.status = :status,
            e.sentAt = :sentAt
        where e.id = :id
    """)
    int markSent(@Param("id") UUID id,
                 @Param("status") OutboxStatus status,
                 @Param("sentAt") Instant sentAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update OutboxEvent e
        set e.attempts = :attempts,
            e.status = :status
        where e.id = :id
    """)
    int markFailedOrRetry(@Param("id") UUID id,
                          @Param("attempts") int attempts,
                          @Param("status") OutboxStatus status);
}
