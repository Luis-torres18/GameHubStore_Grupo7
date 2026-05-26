package com.GameHubStore.payment.service;

import com.GameHubStore.payment.exception.PaymentNotFoundException;
import com.GameHubStore.payment.exception.PaymentValidationException;
import com.GameHubStore.payment.model.dto.PaymentRequest;
import com.GameHubStore.payment.model.dto.PaymentResponse;
import com.GameHubStore.payment.model.entities.Payment;
import com.GameHubStore.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentResponse createPayment(PaymentRequest request) {

        // Evitar pago duplicado aprobado
        if (paymentRepository.existsByOrdenIdAndEstado(request.getOrdenId(), "APPROVED")) {
            throw new PaymentValidationException("Ya existe un pago aprobado para la orden: " + request.getOrdenId());
        }

        // Evitar código de transacción duplicado
        paymentRepository.findByCodigoTransaccion(request.getCodigoTransaccion()).ifPresent(p -> {
            throw new PaymentValidationException("El código de transacción ya existe: " + request.getCodigoTransaccion());
        });

        Payment payment = Payment.builder()
                .ordenId(request.getOrdenId())
                .monto(request.getMonto())
                .metodo(request.getMetodo().toUpperCase())
                .estado("PENDING")
                .codigoTransaccion(request.getCodigoTransaccion())
                .fecha(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Pago creado para ordenId={} con monto={}", request.getOrdenId(), request.getMonto());
        return mapToResponse(saved);
    }

    public List<PaymentResponse> getAllPayments() {
        log.info("Listando todos los pagos");
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    public List<PaymentResponse> getPaymentsByOrdenId(Long ordenId) {
        log.info("Listando pagos de ordenId={}", ordenId);
        return paymentRepository.findByOrdenId(ordenId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByEstado(String estado) {
        log.info("Listando pagos con estado={}", estado);
        return paymentRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse updateEstado(Long id, String estado) {
        Payment payment = findOrThrow(id);

        if ("CANCELLED".equalsIgnoreCase(payment.getEstado())) {
            throw new PaymentValidationException("No se puede modificar un pago anulado.");
        }

        payment.setEstado(estado.toUpperCase());
        log.info("Pago id={} actualizado a estado={}", id, estado);
        return mapToResponse(paymentRepository.save(payment));
    }

    public PaymentResponse cancelPayment(Long id) {
        Payment payment = findOrThrow(id);

        if ("CANCELLED".equalsIgnoreCase(payment.getEstado())) {
            throw new PaymentValidationException("El pago ya está anulado.");
        }
        if ("APPROVED".equalsIgnoreCase(payment.getEstado())) {
            throw new PaymentValidationException("No se puede anular un pago aprobado.");
        }

        payment.setEstado("CANCELLED");
        log.info("Pago id={} anulado", id);
        return mapToResponse(paymentRepository.save(payment));
    }

    private Payment findOrThrow(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago con id={} no encontrado", id);
                    return new PaymentNotFoundException("Pago no encontrado con ID: " + id);
                });
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .ordenId(payment.getOrdenId())
                .monto(payment.getMonto())
                .metodo(payment.getMetodo())
                .estado(payment.getEstado())
                .codigoTransaccion(payment.getCodigoTransaccion())
                .fecha(payment.getFecha())
                .build();
    }
}