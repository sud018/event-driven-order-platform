package org.example.controller;

import org.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/by-order/{orderId}")
    public Object getByOrderId(@PathVariable UUID orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow();
    }
}
