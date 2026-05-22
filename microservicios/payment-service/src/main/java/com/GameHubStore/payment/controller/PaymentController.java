package com.GameHubStore.payment.controller;

import com.GameHubStore.payment.model.dto.PaymentRequest;
import com.GameHubStore.payment.model.dto.PaymentResponse;
import com.GameHubStore.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.createPayment(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.noContent().build();
    }
}