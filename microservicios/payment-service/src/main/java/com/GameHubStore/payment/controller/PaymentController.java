package com.GameHubStore.payment.controller;

import com.GameHubStore.payment.model.dto.PaymentRequest;
import com.GameHubStore.payment.model.dto.PaymentResponse;
import com.GameHubStore.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    // POST /api/payments
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("[PAYMENT-CONTROLLER] POST /api/payments - ordenId={}", request.getOrdenId());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    // GET /api/payments
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // GET /api/payments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/{}", id);
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // GET /api/payments/orden/{ordenId}
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<PaymentResponse>> getByOrdenId(@PathVariable Long ordenId) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/orden/{}", ordenId);
        return ResponseEntity.ok(paymentService.getPaymentsByOrdenId(ordenId));
    }

    // GET /api/payments/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PaymentResponse>> getByEstado(@PathVariable String estado) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/estado/{}", estado);
        return ResponseEntity.ok(paymentService.getPaymentsByEstado(estado));
    }

    // PUT /api/payments/{id}/estado?estado=APPROVED
    @PutMapping("/{id}/estado")
    public ResponseEntity<PaymentResponse> updateEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("[PAYMENT-CONTROLLER] PUT /api/payments/{}/estado - estado={}", id, estado);
        return ResponseEntity.ok(paymentService.updateEstado(id, estado));
    }

    // PATCH /api/payments/{id}/anular
    @PatchMapping("/{id}/anular")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long id) {
        log.info("[PAYMENT-CONTROLLER] PATCH /api/payments/{}/anular", id);
        return ResponseEntity.ok(paymentService.cancelPayment(id));
    }
}