package com.GameHubStore.review_service.service;

import com.GameHubStore.review_service.exception.ReviewInvalidException;
import com.GameHubStore.review_service.exception.ReviewNotFoundException;
import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import com.GameHubStore.review_service.model.entities.Review;
import com.GameHubStore.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        log.info("[review-service] Creating review for product ID: {}", request.getProductId());

        // Validar si ya existe una reseña de este usuario para esta orden y producto
        if (reviewRepository.existsByUserIdAndProductIdAndOrderId(
                request.getUserId(), request.getProductId(), request.getOrderId())) {
            throw new ReviewInvalidException("User already reviewed this product for this order.");
        }

        Review review = Review.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .orderId(request.getOrderId())
                .score(request.getScore())
                .comment(request.getComment())
                .status(true) // Activa por defecto
                .date(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByDateDesc(productId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByDateDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + id));
        return toResponse(review);
    }

    @Override
    public ReviewResponse updateReview(Long id, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + id));

        review.setScore(request.getScore());
        review.setComment(request.getComment());
        review.setDate(LocalDateTime.now()); // Opcional: actualizar fecha de modificación

        Review updated = reviewRepository.save(review);
        return toResponse(updated);
    }

    @Override
    public ReviewResponse moderateReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + id));

        // Cambia el estado (ej. de visible a oculta)
        review.setStatus(!review.getStatus());
        Review moderated = reviewRepository.save(review);
        return toResponse(moderated);
    }

    @Override
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    // --- Mapper ---
    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .productId(review.getProductId())
                .orderId(review.getOrderId())
                .score(review.getScore())
                .comment(review.getComment())
                .status(review.getStatus())
                .date(review.getDate())
                .build();
    }
}