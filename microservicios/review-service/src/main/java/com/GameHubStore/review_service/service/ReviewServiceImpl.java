package com.GameHubStore.review_service.service;

import com.GameHubStore.review_service.exception.ReviewNotFoundException;
import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
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
    public List<ReviewResponse> findAll() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toResponse) // Asumiendo que tienes este método de mapeo
                .collect(Collectors.toList());
    }
    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        log.info("[review-service] Creating new review for product: {}", request.getProductId());

        Review review = Review.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .orderId(request.getOrderId())
                .score(request.getScore())
                .comment(request.getComment())
                .status(true) // By default active
                .date(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);
        log.info("[review-service] Review created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        log.info("[review-service] Listing reviews for product: {}", productId);
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        log.info("[review-service] Listing reviews by user: {}", userId);
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        log.info("[review-service] Searching review by ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + id));
        return toResponse(review);
    }

    @Override
    public ReviewResponse updateReview(Long id, UpdateReviewRequest request) {
        log.info("[review-service] Updating review ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + id));

        review.setScore(request.getScore());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);
        log.info("[review-service] Review updated ID: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public ReviewResponse moderateReview(Long id) {
        log.info("[review-service] Moderating (hiding) review ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + id));

        review.setStatus(false); // Disable review
        Review saved = reviewRepository.save(review);
        log.info("[review-service] Review moderated (status set to false) ID: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public String deleteReview(Long id) {
        log.info("[review-service] Deleting review ID: {}", id);
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Cannot delete. Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
        log.info("[review-service] Review deleted ID: {}", id);

        return "Reseña con id: "+ id + " eliminada" ;
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .productId(r.getProductId())
                .orderId(r.getOrderId())
                .score(r.getScore())
                .comment(r.getComment())
                .status(r.getStatus())
                .date(r.getDate())
                .build();
    }
}