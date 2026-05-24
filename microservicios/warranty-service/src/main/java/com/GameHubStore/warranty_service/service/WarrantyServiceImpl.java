package com.GameHubStore.warranty_service.service;

import com.GameHubStore.warranty_service.exception.WarrantyInvalidException;
import com.GameHubStore.warranty_service.exception.WarrantyNotFoundException;
import com.GameHubStore.warranty_service.model.dto.CloseWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.UpdateWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyResponse;
import com.GameHubStore.warranty_service.model.entities.Warranty;
import com.GameHubStore.warranty_service.repository.WarrantyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarrantyServiceImpl implements WarrantyService {

    private static final Logger log = LoggerFactory.getLogger(WarrantyServiceImpl.class);

    private static final int WARRANTY_MONTHS = 12;
    private static final String STATUS_CLOSED = "CLOSED";

    private final WarrantyRepository warrantyRepository;

    @Override
    public List<WarrantyResponse> findAll() {
        return warrantyRepository.findAll()
                .stream()
                .map(this::toResponse) // Asumiendo que tienes este método de mapeo
                .collect(Collectors.toList());
    }


    @Override
    public WarrantyResponse createWarranty(WarrantyRequest request) {
        log.info("[warranty-service] Creating warranty for userId={} orderId={} productId={}",
                request.getUserId(), request.getOrderId(), request.getProductId());

        // Business rule: no warranty without an associated purchase
        // -> orderClient.findById(request.getOrderId()) must return a valid order
        //    containing the product; throw WarrantyInvalidException if not found.

        // Business rule: warranty request must be within the allowed period
        // -> order.getDate().plusMonths(WARRANTY_MONTHS).isAfter(LocalDate.now())
        //    throw WarrantyInvalidException("La garantía ha vencido, plazo de " + WARRANTY_MONTHS + " meses superado")

        Warranty warranty = Warranty.builder()
                .userId(request.getUserId())
                .orderId(request.getOrderId())
                .productId(request.getProductId())
                .reason(request.getReason())
                .status("PENDING")
                .requestDate(LocalDate.now())
                .build();

        Warranty saved = warrantyRepository.save(warranty);
        log.info("[warranty-service] Warranty created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<WarrantyResponse> findByUser(Long userId) {
        log.info("[warranty-service] Listing warranties for userId={}", userId);
        return warrantyRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarrantyResponse> findByProduct(Long productId) {
        log.info("[warranty-service] Listing warranties for productId={}", productId);
        return warrantyRepository.findByProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WarrantyResponse> findByStatus(String status) {
        log.info("[warranty-service] Listing warranties with status={}", status);
        return warrantyRepository.findByStatus(status.toUpperCase()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarrantyResponse findById(Long id) {
        log.info("[warranty-service] Searching warranty with ID: {}", id);
        Warranty warranty = warrantyRepository.findById(id)
                .orElseThrow(() -> new WarrantyNotFoundException("Garantía no encontrada con ID: " + id));
        return toResponse(warranty);
    }

    @Override
    public WarrantyResponse updateWarranty(Long id, UpdateWarrantyRequest request) {
        log.info("[warranty-service] Updating status/diagnosis for warrantyId={}", id);
        Warranty warranty = warrantyRepository.findById(id)
                .orElseThrow(() -> new WarrantyNotFoundException("Garantía no encontrada con ID: " + id));

        // Business rule: cannot modify a closed warranty
        if (STATUS_CLOSED.equals(warranty.getStatus())) {
            throw new WarrantyInvalidException("No se puede actualizar una garantía ya cerrada");
        }

        warranty.setStatus(request.getStatus().toUpperCase());
        if (request.getDiagnosis() != null) {
            warranty.setDiagnosis(request.getDiagnosis());
        }

        Warranty updated = warrantyRepository.save(warranty);
        log.info("[warranty-service] Warranty updated ID: {} -> status: {}", updated.getId(), updated.getStatus());
        return toResponse(updated);
    }

    @Override
    public WarrantyResponse closeWarranty(Long id, CloseWarrantyRequest request) {
        log.info("[warranty-service] Closing warranty with ID: {}", id);
        Warranty warranty = warrantyRepository.findById(id)
                .orElseThrow(() -> new WarrantyNotFoundException("Garantía no encontrada con ID: " + id));

        // Business rule: cannot close an already closed warranty
        if (STATUS_CLOSED.equals(warranty.getStatus())) {
            throw new WarrantyInvalidException("La garantía ya está cerrada");
        }

        // Business rule: cannot close without a resolution
        if (request.getResolution() == null || request.getResolution().isBlank()) {
            throw new WarrantyInvalidException("No se puede cerrar una garantía sin resolución");
        }

        warranty.setResolution(request.getResolution());
        warranty.setStatus(STATUS_CLOSED);

        Warranty saved = warrantyRepository.save(warranty);
        log.info("[warranty-service] Warranty closed with ID: {}", saved.getId());
        return toResponse(saved);
    }

    // ─── Mapper ───────────────────────────────────────────────────────────────
    private WarrantyResponse toResponse(Warranty w) {
        return WarrantyResponse.builder()
                .id(w.getId())
                .userId(w.getUserId())
                .orderId(w.getOrderId())
                .productId(w.getProductId())
                .reason(w.getReason())
                .status(w.getStatus())
                .requestDate(w.getRequestDate())
                .diagnosis(w.getDiagnosis())
                .resolution(w.getResolution())
                .build();
    }
}