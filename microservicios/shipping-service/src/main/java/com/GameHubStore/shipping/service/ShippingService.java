package com.GameHubStore.shipping.service;

import com.GameHubStore.shipping.exception.ShippingNotFoundException;
import com.GameHubStore.shipping.exception.ShippingValidationException;
import com.GameHubStore.shipping.model.dto.ShippingRequest;
import com.GameHubStore.shipping.model.dto.ShippingResponse;
import com.GameHubStore.shipping.model.entities.Shipping;
import com.GameHubStore.shipping.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingService {

    private final ShippingRepository shippingRepository;

    // ─── Crear despacho ───────────────────────────────────────────────────────────
    @Transactional
    public ShippingResponse createShipping(ShippingRequest request) {
        log.info("[SHIPPING-SERVICE] Creating shipping for ordenId={}", request.getOrdenId());

        // Regla: solo despachar órdenes pagadas
        if (!request.getEstadoOrden().equalsIgnoreCase("PAID")) {
            log.warn("[SHIPPING-SERVICE] Order is not paid. estadoOrden={}", request.getEstadoOrden());
            throw new ShippingValidationException("Only paid orders can be shipped. Current status: " + request.getEstadoOrden());
        }

        // Regla: dirección válida obligatoria
        if (request.getDireccion() == null || request.getDireccion().isBlank()) {
            log.warn("[SHIPPING-SERVICE] Invalid address for ordenId={}", request.getOrdenId());
            throw new ShippingValidationException("A valid address is required for shipping");
        }

        // Regla: tracking único generado automáticamente
        String tracking = UUID.randomUUID().toString();

        Shipping shipping = Shipping.builder()
                .ordenId(request.getOrdenId())
                .usuarioId(request.getUsuarioId())
                .direccion(request.getDireccion())
                .transportista(request.getTransportista())
                .estado("PENDIENTE")
                .tracking(tracking)
                .fechaEnvio(LocalDateTime.now())
                .build();

        Shipping saved = shippingRepository.save(shipping);
        log.info("[SHIPPING-SERVICE] Shipping created id={}, tracking={}", saved.getId(), tracking);
        return mapToResponse(saved);
    }

    // ─── Listar despachos por orden o estado ──────────────────────────────────────
    public List<ShippingResponse> getShippings(Long ordenId, String estado) {
        log.info("[SHIPPING-SERVICE] Fetching shippings ordenId={}, estado={}", ordenId, estado);

        if (ordenId != null) {
            return shippingRepository.findByOrdenId(ordenId).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        if (estado != null) {
            return shippingRepository.findByEstado(estado).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return shippingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Buscar despacho por ID ───────────────────────────────────────────────────
    public ShippingResponse getShippingById(Long id) {
        log.info("[SHIPPING-SERVICE] Fetching shipping id={}", id);
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[SHIPPING-SERVICE] Shipping not found id={}", id);
                    return new ShippingNotFoundException("Shipping not found with ID: " + id);
                });
        return mapToResponse(shipping);
    }

    // ─── Actualizar estado y tracking ─────────────────────────────────────────────
    @Transactional
    public ShippingResponse updateShippingStatus(Long id, String estado, String tracking) {
        log.info("[SHIPPING-SERVICE] Updating shipping id={} to estado={}", id, estado);

        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new ShippingNotFoundException("Shipping not found with ID: " + id));

        // Regla: no cambiar a entregado sin fecha de entrega
        if (estado.equalsIgnoreCase("ENTREGADO") && shipping.getFechaEnvio() == null) {
            log.warn("[SHIPPING-SERVICE] Cannot set ENTREGADO without fechaEnvio id={}", id);
            throw new ShippingValidationException("Cannot set ENTREGADO without a valid shipping date");
        }

        // Si pasa a ENTREGADO se registra la fecha de entrega
        if (estado.equalsIgnoreCase("ENTREGADO")) {
            shipping.setFechaEntrega(LocalDateTime.now());
            log.info("[SHIPPING-SERVICE] Setting fechaEntrega for id={}", id);
        }

        shipping.setEstado(estado);

        // Regla: tracking único cuando exista
        if (tracking != null && !tracking.isBlank()) {
            if (shippingRepository.existsByTracking(tracking) &&
                    !tracking.equals(shipping.getTracking())) {
                log.warn("[SHIPPING-SERVICE] Tracking already exists: {}", tracking);
                throw new ShippingValidationException("Tracking number already exists: " + tracking);
            }
            shipping.setTracking(tracking);
        }

        Shipping updated = shippingRepository.save(shipping);
        log.info("[SHIPPING-SERVICE] Shipping updated id={}", id);
        return mapToResponse(updated);
    }

    // ─── Cancelar despacho si la orden fue anulada ────────────────────────────────
    @Transactional
    public void cancelShipping(Long id) {
        log.info("[SHIPPING-SERVICE] Cancelling shipping id={}", id);

        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new ShippingNotFoundException("Shipping not found with ID: " + id));

        // Regla: no cancelar un despacho ya entregado
        if (shipping.getEstado().equalsIgnoreCase("ENTREGADO")) {
            log.warn("[SHIPPING-SERVICE] Cannot cancel a delivered shipping id={}", id);
            throw new ShippingValidationException("Cannot cancel a shipping that has already been delivered");
        }

        shipping.setEstado("CANCELADO");
        shippingRepository.save(shipping);
        log.info("[SHIPPING-SERVICE] Shipping cancelled id={}", id);
    }

    // ─── Helper: mapear entidad a response ───────────────────────────────────────
    private ShippingResponse mapToResponse(Shipping shipping) {
        return ShippingResponse.builder()
                .id(shipping.getId())
                .ordenId(shipping.getOrdenId())
                .usuarioId(shipping.getUsuarioId())
                .direccion(shipping.getDireccion())
                .transportista(shipping.getTransportista())
                .tracking(shipping.getTracking())
                .estado(shipping.getEstado())
                .fechaEnvio(shipping.getFechaEnvio())
                .fechaEntrega(shipping.getFechaEntrega())
                .build();
    }
}