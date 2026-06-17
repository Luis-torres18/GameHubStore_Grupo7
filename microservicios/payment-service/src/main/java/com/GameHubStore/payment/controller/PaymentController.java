package com.GameHubStore.payment.controller;

import com.GameHubStore.payment.model.dto.PaymentRequest;
import com.GameHubStore.payment.model.dto.PaymentResponse;
import com.GameHubStore.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Crear un pago", description = "Registra un nuevo pago para una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago creado con exito"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "422", description ="Pago duplicado o monto incorrecto" )
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("[PAYMENT-CONTROLLER] POST /api/payments - ordenId={}", request.getOrdenId());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    // GET /api/payments
    @Operation(summary = "Listar todos los pagos", description = "Obtiene una lista de todos los  pagos")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // GET /api/payments/{id}
    @Operation(summary = "Buscar pago por ID", description = "Obtiene un pago especifico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encpontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/{}", id);
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // GET /api/payments/orden/{ordenId}
    @Operation(summary = "Listar pagos por orden ", description = "Obtiene los pagos de una orden especifica")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<PaymentResponse>> getByOrdenId(@PathVariable Long ordenId) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/orden/{}", ordenId);
        return ResponseEntity.ok(paymentService.getPaymentsByOrdenId(ordenId));
    }

    // GET /api/payments/estado/{estado}
    @Operation(summary = "Listar pagos por estado", description = "Obtiene pagos filtrados por un estado (PENDING, APROVED , CANCELLED)")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PaymentResponse>> getByEstado(@PathVariable String estado) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/estado/{}", estado);
        return ResponseEntity.ok(paymentService.getPaymentsByEstado(estado));
    }

    // PUT /api/payments/{id}/estado?estado=APPROVED
    @Operation(summary = "Actualizar estado de un pago ", description = "Cambia el estado de un pago existente ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "422", description ="No se puede modificar el pago" )
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<PaymentResponse> updateEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("[PAYMENT-CONTROLLER] PUT /api/payments/{}/estado - estado={}", id, estado);
        return ResponseEntity.ok(paymentService.updateEstado(id, estado));
    }

    // PATCH /api/payments/{id}/anular
    @Operation(summary = "Anular un pago", description = "Cambia el estado del pago a CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago anulado con exito"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "422", description ="No se puede anular el pago                                                                                                                                                        " )
    })
    @PatchMapping("/{id}/anular")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long id) {
        log.info("[PAYMENT-CONTROLLER] PATCH /api/payments/{}/anular", id);
        return ResponseEntity.ok(paymentService.cancelPayment(id));
    }
}