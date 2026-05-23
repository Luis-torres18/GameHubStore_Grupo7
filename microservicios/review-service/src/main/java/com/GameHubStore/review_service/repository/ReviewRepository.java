package com.GameHubStore.review_service.repository;

import com.GameHubStore.review_service.model.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // List reviews by product, ordered by newest first
    List<Review> findByProductIdOrderByDateDesc(Long productId);

    // List reviews by user, ordered by newest first
    List<Review> findByUserIdOrderByDateDesc(Long userId);

    // Business rule: Check if a user already reviewed this product from this specific order
    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);
}