package com.GameHubStore.warranty_service.service;

import com.GameHubStore.warranty_service.model.dto.CloseWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.UpdateWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyResponse;

import java.util.List;

public interface WarrantyService {

    WarrantyResponse createWarranty(WarrantyRequest request);

    List<WarrantyResponse> findByUser(Long userId);

    List<WarrantyResponse> findByProduct(Long productId);

    List<WarrantyResponse> findByStatus(String status);

    WarrantyResponse findById(Long id);

    WarrantyResponse updateWarranty(Long id, UpdateWarrantyRequest request);

    WarrantyResponse closeWarranty(Long id, CloseWarrantyRequest request);
}