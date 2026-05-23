package com.GameHubStore.review_service.service;

import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    List<ReviewResponse> getReviewsByProduct(Long productId);

    List<ReviewResponse> getReviewsByUser(Long userId);

    ReviewResponse getReviewById(Long id);

    ReviewResponse updateReview(Long id, UpdateReviewRequest request);

    ReviewResponse moderateReview(Long id);

    void deleteReview(Long id);
}