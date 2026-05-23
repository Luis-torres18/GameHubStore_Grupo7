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

    // Find promotion by unique code
    Optional<Promotion> findByCode(String code);

    // Check if a promotion exists by code
    boolean existsByCode(String code);

    // Find active promotions within date range
    // Maps to: status = true AND startDate <= ? AND endDate >= ?
    List<Promotion> findByStatusTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate today1, LocalDate today2);

    // List all history ordered by start date (newest first)
    List<Promotion> findAllByOrderByStartDateDesc();

    // Filter by status (active or inactive)
    List<Promotion> findByStatus(Boolean status);
}