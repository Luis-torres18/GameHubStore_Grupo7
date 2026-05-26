package com.GameHubStore.shipping.repository;

import com.GameHubStore.shipping.model.entities.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    List<Shipping> findByOrderId(Long orderId);

    List<Shipping> findByEstado(String status);
}