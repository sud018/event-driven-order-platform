package org.example.dto;

import org.example.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID id, BigDecimal amount, OrderStatus status, Instant createdAt) {}
