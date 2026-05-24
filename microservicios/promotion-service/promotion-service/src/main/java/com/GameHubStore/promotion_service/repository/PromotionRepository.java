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


    Optional<Promotion> findByCode(String code);


    boolean existsByCode(String code);


    List<Promotion> findByStatusTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate today1, LocalDate today2);

    List<Promotion> findAllByOrderByStartDateDesc();


    List<Promotion> findByStatus(Boolean status);
}