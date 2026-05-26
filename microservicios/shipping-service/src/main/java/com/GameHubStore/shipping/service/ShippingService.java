package com.GameHubStore.shipping.service;

import com.GameHubStore.shipping.exception.ShippingNotFoundException;
import com.GameHubStore.shipping.model.dto.ShippingRequest;
import com.GameHubStore.shipping.model.dto.ShippingResponse;
import com.GameHubStore.shipping.model.entities.Shipping;
import com.GameHubStore.shipping.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShippingRepository shippingRepository;

    public ShippingResponse createShipping(ShippingRequest request) {
        Shipping shipping = Shipping.builder()
                .orderId(request.getOrderId())
                .usuarioId(request.getUsuarioId())
                .direccion(request.getDireccion())
                .transportista(request.getTransportista())
                .estado("PENDIENTE")
                .tracking(UUID.randomUUID().toString())
                .fechaEnvio(LocalDateTime.now())
                .build();

        Shipping savedShipping = shippingRepository.save(shipping);
        return mapToResponse(savedShipping);
    }

    public List<ShippingResponse> getShippings(Long orderId, String estado) {
        if (orderId != null) {
            return shippingRepository.findByOrderId(orderId).stream()
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

    public ShippingResponse getShippingById(Long id) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new ShippingNotFoundException("Shipping not found with ID: " + id));
        return mapToResponse(shipping);
    }

    public ShippingResponse updateShippingStatus(Long id, String estado, String tracking) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new ShippingNotFoundException("Shipping not found with ID: " + id));

        shipping.setEstado(estado);
        if (tracking != null) {
            shipping.setTracking(tracking);
        }

        Shipping updatedShipping = shippingRepository.save(shipping);
        return mapToResponse(updatedShipping);
    }

    public void cancelShipping(Long id) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new ShippingNotFoundException("Shipping not found with ID: " + id));

        shipping.setEstado("CANCELADO");
        shippingRepository.save(shipping);
    }

    private ShippingResponse mapToResponse(Shipping shipping) {
        return ShippingResponse.builder()
                .id(shipping.getId())
                .orderId(shipping.getOrderId())
                .userId(shipping.getUsuarioId())
                .direccion(shipping.getDireccion())
                .transportista(shipping.getTransportista())
                .tracking(shipping.getTracking())
                .status(shipping.getEstado())
                .fechaEnvio(shipping.getFechaEnvio())
                .build();
    }
}