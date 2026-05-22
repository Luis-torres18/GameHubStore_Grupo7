package com.GameHubStore.promotion_service.repository;

import com.GameHubStore.promotion_service.model.entities.Promotion;
import com.GameHubStore.promotion_service.model.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    // Promociones activas cuya fecha es vigente
    List<Promotion> findByEstadoTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            LocalDate hoy1, LocalDate hoy2);

    // Historial: todas sin filtro de estado
    List<Promotion> findAllByOrderByFechaInicioDesc();

    List<Promotion> findByEstado(Boolean estado);
}