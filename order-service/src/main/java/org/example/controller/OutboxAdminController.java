package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.OutboxEventResponse;
import org.example.outbox.OutboxEvent;
import org.example.outbox.OutboxStatus;
import org.example.repository.OutboxEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outbox")
@RequiredArgsConstructor
public class OutboxAdminController {

    private final OutboxEventRepository outboxRepo;

    @GetMapping("/recent")
    public List<OutboxEventResponse> recent(@RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return outboxRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, safeLimit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/failed")
    public List<OutboxEventResponse> failed(@RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return outboxRepo.findByStatusOrderByCreatedAtDesc(OutboxStatus.FAILED, PageRequest.of(0, safeLimit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/pending")
    public List<OutboxEventResponse> pending(@RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return outboxRepo.findByStatusOrderByCreatedAtDesc(OutboxStatus.PENDING, PageRequest.of(0, safeLimit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OutboxEventResponse toResponse(OutboxEvent e) {
        String payload = e.getPayload() == null ? "" : e.getPayload();
        int len = payload.length();
        String preview = payload.substring(0, Math.min(len, 160)); // keep it short for UI/logs

        return new OutboxEventResponse(
                e.getId(),
                e.getAggregateType(),
                e.getAggregateId(),
                e.getEventType(),
                e.getTopic(),
                e.getStatus(),
                e.getAttempts(),
                e.getCreatedAt(),
                e.getSentAt(),
                len,
                preview
        );
    }
}

