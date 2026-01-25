package org.example.dto;

import org.example.model.OrderStatus;

import java.util.UUID;

public record CreateOrderResponse(UUID orderId, OrderStatus status) { }