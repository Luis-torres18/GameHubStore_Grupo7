package com.GameHubStore.order.controller;

import com.GameHubStore.order.assemblers.OrderModelAssembler;
import com.GameHubStore.order.model.dto.OrderRequest;
import com.GameHubStore.order.model.dto.OrderResponse;
import com.GameHubStore.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderService orderService;
    private final OrderModelAssembler assembler; // Inyectamos el Assembler


    @GetMapping
    @Operation(summary = "Listar todas las ordenes", description = "Retorna la lista completa de ordenes")
    @ApiResponse(responseCode = "200", description = "Listado exitoso")
    public ResponseEntity<CollectionModel<EntityModel<OrderResponse>>> getAllOrders() {
        List<EntityModel<OrderResponse>> orders = orderService.getAllOrders().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel()));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar orden por ID", description = "Rertorna una orden especifica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="200", description = "Orden encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<CollectionModel<EntityModel<OrderResponse>>> getOrderById(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long id) {

        OrderResponse response = orderService.getOrderById(id);
        EntityModel<OrderResponse> entityModel = assembler.toModel(response);

        // Mantenemos tu lógica de devolver una lista con un solo elemento
        return ResponseEntity.ok(CollectionModel.of(List.of(entityModel),
                linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel()));
    }

    @GetMapping("/usuario/{userId}")
    @Operation(summary = "Listar ordenes por usuario", description = "Retorna todas las ordenes de un usuario especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado exitoso"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<CollectionModel<EntityModel<OrderResponse>>> getOrdersByUserId(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {

        List<EntityModel<OrderResponse>> orders = orderService.getOrdersByUserId(userId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).getOrdersByUserId(userId)).withSelfRel()));
    }


    @GetMapping("/estado/{status}")
    @Operation(summary = "Listar ordenes por estado", description = "Retorna ordenes filtradas por estado : PENDING, PAID, CANCELLED")
    @ApiResponse(responseCode ="200" , description ="Listado exitoso" )
    public ResponseEntity<CollectionModel<EntityModel<OrderResponse>>> getOrdersByStatus(
            @Parameter(description = "Estado de la orden", required = true, example = "PENDING")
            @PathVariable String status) {

        List<EntityModel<OrderResponse>> orders = orderService.getOrdersByStatus(status).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).getOrdersByStatus(status)).withSelfRel()));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear orden", description = "Crear una nueva orden de compra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    public void createOrder(@Valid @RequestBody OrderRequest request) {
        orderService.createOrder(request);
    }


    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado", description = "Actualiza el estado de una orden de compra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede modificar una orden pagada"),
            @ApiResponse(responseCode = "404", description ="Orden no encontrada")
    })
    public ResponseEntity<EntityModel<OrderResponse>> updateStatus(
            @Parameter(description ="ID de la orden", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado",required = true, example = "PAID")
            @RequestParam String status) {

        OrderResponse response = orderService.updateStatus(id, status);
        return ResponseEntity.ok(assembler.toModel(response));
    }


    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar Orden", description = "Cancela una orden cambiando su estado a CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden cancelada exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede cancelar una orden pagada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<EntityModel<OrderResponse>> cancelOrder(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long id) {

        OrderResponse response = orderService.cancelOrder(id);
        return ResponseEntity.ok(assembler.toModel(response));
    }
}