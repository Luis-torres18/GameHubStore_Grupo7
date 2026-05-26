package com.GameHubStore.payment.service;

import com.GameHubStore.payment.exception.PaymentNotFoundException;
import com.GameHubStore.payment.exception.PaymentValidationException;
import com.GameHubStore.payment.model.dto.PaymentRequest;
import com.GameHubStore.payment.model.dto.PaymentResponse;
import com.GameHubStore.payment.model.entities.Payment;
import com.GameHubStore.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse createPayment(PaymentRequest request) {
        if (paymentRepository.existsByOrderIdAndStatus(request.getOrderId(), "APROBADO")) {
            throw new PaymentValidationException("La orden ya tiene un pago aprobado.");
        }

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .method(request.getMethod())
                .status("APROBADO")
                .transactionCode(UUID.randomUUID().toString())
                .date(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pago no encontrado con ID: " + id));
        return mapToResponse(payment);
    }

    public PaymentResponse updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pago no encontrado con ID: " + id));

        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToResponse(updatedPayment);
    }

    public void cancelPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pago no encontrado con ID: " + id));

        if ("ANULADO".equals(payment.getStatus())) {
            throw new PaymentValidationException("El pago ya se encuentra anulado.");
        }

        payment.setStatus("ANULADO");
        paymentRepository.save(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionCode(payment.getTransactionCode())
                .date(payment.getDate())
                .build();
    }
}