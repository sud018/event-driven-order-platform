package org.example.controller;

import org.example.dto.CreateOrderRequest;
import org.example.dto.CreateOrderResponse;
import org.example.repository.OrderRepository;
import org.example.model.OrderStatus;
import org.example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import org.example.dto.OrderResponse;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {

        UUID orderId = orderService.createOrder(request.amount());

        return new CreateOrderResponse(orderId, OrderStatus.CREATED);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id) {
        var order = orderRepository.findById(id).orElseThrow();
        return new OrderResponse(order.getId(), order.getAmount(), order.getStatus(), order.getCreatedAt());
    }

    @GetMapping
    public List<OrderResponse> list() {
        return orderRepository.findAll().stream()
                .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(20)
                .map(o -> new OrderResponse(o.getId(), o.getAmount(), o.getStatus(), o.getCreatedAt()))
                .toList();
    }
}

