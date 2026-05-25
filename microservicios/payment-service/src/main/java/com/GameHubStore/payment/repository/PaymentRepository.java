package com.GameHubStore.payment.repository;

import com.GameHubStore.payment.model.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrdenId(Long ordenId);
    List<Payment> findByEstado(String estado);
    Optional<Payment> findByCodigoTransaccion(String codigoTransaccion);
    boolean existsByOrdenIdAndEstado(Long ordenId, String estado);
}