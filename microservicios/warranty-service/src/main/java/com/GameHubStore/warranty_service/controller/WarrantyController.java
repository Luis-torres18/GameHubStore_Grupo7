package com.GameHubStore.warranty_service.controller;

import com.GameHubStore.warranty_service.model.dto.CloseWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.UpdateWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyResponse;
import com.GameHubStore.warranty_service.service.WarrantyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/warranty")
@RequiredArgsConstructor
public class WarrantyController {

    private final WarrantyService warrantyService;

    // crear garantia
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarrantyResponse createWarranty(@RequestBody @Valid WarrantyRequest request) {
        return warrantyService.createWarranty(request);
    }

    // listar todas las garantias
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WarrantyResponse> findAllWarranties() {
        return warrantyService.findAll();
    }

    // listar garantias garantia por usuario
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<WarrantyResponse> findByUser(@PathVariable Long userId) {
        return warrantyService.findByUser(userId);
    }

    // listar garantias por producto
    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public List<WarrantyResponse> findByProduct(@PathVariable Long productId) {
        return warrantyService.findByProduct(productId);
    }

    // listar garantias por estatus
    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public List<WarrantyResponse> findByStatus(@PathVariable String status) {
        return warrantyService.findByStatus(status);
    }

    // find garantia por id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<WarrantyResponse> findById(@PathVariable Long id) {
        return Collections.singletonList(warrantyService.findById(id));
    }

    // actualizar garantia
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarrantyResponse updateWarranty(
            @PathVariable Long id,
            @RequestBody @Valid UpdateWarrantyRequest request) {
        return warrantyService.updateWarranty(id, request);
    }

    // cerrar garantia
    @PatchMapping("/{id}/close")
    @ResponseStatus(HttpStatus.OK)
    public WarrantyResponse closeWarranty(
            @PathVariable Long id,
            @RequestBody @Valid CloseWarrantyRequest request) {
        return warrantyService.closeWarranty(id, request);
    }
}