package com.GameHubStore.shipping.repository;

import com.GameHubStore.shipping.model.entities.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    // Listar por orden
    List<Shipping> findByOrdenId(Long ordenId);

    // Listar por estado
    List<Shipping> findByEstado(String estado);

    // Regla: tracking único
    boolean existsByTracking(String tracking);
}