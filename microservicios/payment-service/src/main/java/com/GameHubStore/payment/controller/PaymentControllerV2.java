package com.GameHubStore.payment.controller;

import com.GameHubStore.payment.assemblers.PaymentModelAssembler;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentControllerV2 {

    private static final Logger log = LoggerFactory.getLogger(PaymentControllerV2.class);
    private final PaymentService paymentService;
    private final PaymentModelAssembler assembler; // Inyección del Assembler

    // POST /api/payments
    @Operation(summary = "Crear un pago", description = "Registra un nuevo pago para una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago creado con exito"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "422", description ="Pago duplicado o monto incorrecto" )
    })
    @PostMapping
    public ResponseEntity<EntityModel<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("[PAYMENT-CONTROLLER] POST /api/payments - ordenId={}", request.getOrdenId());
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(response));
    }

    // GET /api/payments
    @Operation(summary = "Listar todos los pagos", description = "Obtiene una lista de todos los pagos")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PaymentResponse>>> getAllPayments() {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments");
        List<EntityModel<PaymentResponse>> payments = paymentService.getAllPayments().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(payments,
                linkTo(methodOn(PaymentController.class).getAllPayments()).withSelfRel()));
    }

    // GET /api/payments/{id}
    @Operation(summary = "Buscar pago por ID", description = "Obtiene un pago especifico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encpontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/{}", id);
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    // GET /api/payments/orden/{ordenId}
    @Operation(summary = "Listar pagos por orden", description = "Obtiene los pagos de una orden especifica")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<CollectionModel<EntityModel<PaymentResponse>>> getByOrdenId(@PathVariable Long ordenId) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/orden/{}", ordenId);
        List<EntityModel<PaymentResponse>> payments = paymentService.getPaymentsByOrdenId(ordenId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(payments,
                linkTo(methodOn(PaymentController.class).getByOrdenId(ordenId)).withSelfRel()));
    }

    // GET /api/payments/estado/{estado}
    @Operation(summary = "Listar pagos por estado", description = "Obtiene pagos filtrados por un estado (PENDING, APROVED , CANCELLED)")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<EntityModel<PaymentResponse>>> getByEstado(@PathVariable String estado) {
        log.info("[PAYMENT-CONTROLLER] GET /api/payments/estado/{}", estado);
        List<EntityModel<PaymentResponse>> payments = paymentService.getPaymentsByEstado(estado).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(payments,
                linkTo(methodOn(PaymentController.class).getByEstado(estado)).withSelfRel()));
    }

    // PUT /api/payments/{id}/estado?estado=APPROVED
    @Operation(summary = "Actualizar estado de un pago", description = "Cambia el estado de un pago existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "422", description ="No se puede modificar el pago" )
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<EntityModel<PaymentResponse>> updateEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("[PAYMENT-CONTROLLER] PUT /api/payments/{}/estado - estado={}", id, estado);
        PaymentResponse response = paymentService.updateEstado(id, estado);
        return ResponseEntity.ok(assembler.toModel(response));
    }

    // PATCH /api/payments/{id}/anular
    @Operation(summary = "Anular un pago", description = "Cambia el estado del pago a CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago anulado con exito"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "422", description ="No se puede anular el pago" )
    })
    @PatchMapping("/{id}/anular")
    public ResponseEntity<EntityModel<PaymentResponse>> cancelPayment(@PathVariable Long id) {
        log.info("[PAYMENT-CONTROLLER] PATCH /api/payments/{}/anular", id);
        PaymentResponse response = paymentService.cancelPayment(id);
        return ResponseEntity.ok(assembler.toModel(response));
    }
}