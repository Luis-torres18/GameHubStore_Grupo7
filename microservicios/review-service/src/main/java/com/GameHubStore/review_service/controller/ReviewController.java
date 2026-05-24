package com.GameHubStore.review_service.controller;

import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import com.GameHubStore.review_service.service.ReviewService;
import feign.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Create review
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(@RequestBody @Valid ReviewRequest request) {
        return reviewService.createReview(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> findAllReviews() {
        return reviewService.findAll();
    }
    // Get reviews by product
    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    // Get reviews by user
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewsByUser(@PathVariable Long userId) {
        return reviewService.getReviewsByUser(userId);
    }

    // Get review by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    // Update review
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse updateReview(
            @PathVariable Long id,
            @RequestBody @Valid UpdateReviewRequest request) {
        return reviewService.updateReview(id, request);
    }

    // Moderate review (admin)
    @PatchMapping("/{id}/moderate")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponse moderateReview(@PathVariable Long id) {
        return reviewService.moderateReview(id);
    }

    // Delete review
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        String message  = reviewService.deleteReview(id);
        return ResponseEntity.ok(message);
    }
}