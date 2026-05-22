package com.GameHubStore.shipping.controller;

import com.GameHubStore.shipping.model.dto.ShippingRequest;
import com.GameHubStore.shipping.model.dto.ShippingResponse;
import com.GameHubStore.shipping.service.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shippings")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping
    public ResponseEntity<ShippingResponse> createShipping(@Valid @RequestBody ShippingRequest request) {
        return new ResponseEntity<>(shippingService.createShipping(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ShippingResponse>> getShippings(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(shippingService.getShippings(orderId, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingResponse> getShippingById(@PathVariable Long id) {
        return ResponseEntity.ok(shippingService.getShippingById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ShippingResponse> updateShippingStatusAndTracking(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String tracking) {
        return ResponseEntity.ok(shippingService.updateShippingStatus(id, estado, tracking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelShipping(@PathVariable Long id) {
        shippingService.cancelShipping(id);
        return ResponseEntity.noContent().build();
    }
}