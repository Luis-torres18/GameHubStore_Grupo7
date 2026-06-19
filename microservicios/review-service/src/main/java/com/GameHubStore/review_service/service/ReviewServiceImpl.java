package com.GameHubStore.review_service.service;

import com.GameHubStore.review_service.client.OrderClient;
import com.GameHubStore.review_service.client.ProductClient;
import com.GameHubStore.review_service.client.UserClient;
import com.GameHubStore.review_service.client.dto.OrderResponse;
import com.GameHubStore.review_service.client.dto.ProductResponse;
import com.GameHubStore.review_service.client.dto.UserResponse;
import com.GameHubStore.review_service.exception.ReviewNotFoundException;
import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
import com.GameHubStore.review_service.model.entities.Review;
import com.GameHubStore.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.flogger.Flogger;
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

    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderClient orderClient;

    @Override
    public List<ReviewResponse> findAll() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public ReviewResponse createReview(ReviewRequest request) {

        UserResponse usuario = userClient.getUserById(request.getUserId());
        if(usuario ==  null){
            log.warn("Intento de reseña inexistente, userId={}",request.getUserId());
            throw new IllegalArgumentException("El usuario con ID "+request.getUserId()+ " no existe");

        }
        if (!Boolean.TRUE.equals(usuario.getEstado())){
            throw new IllegalArgumentException("El usuario con ID "+request.getUserId()+ " esta inactivo");
        }

        List<ProductResponse> productos = productClient.getProductById(request.getProductId());
        if(productos.isEmpty()){
            log.warn("Intento de reseña con producto inexistente, productId={}",request.getProductId());
            throw new IllegalArgumentException("El producto con ID "+request.getProductId()+ " no existe");
        }

        List<OrderResponse> ordenes = orderClient.getOrderById(request.getOrderId());
        if(ordenes.isEmpty()){
            throw new IllegalArgumentException("La orden con ID "+request.getOrderId()+ " no existe");
        }
        OrderResponse orden = ordenes.get(0);

        if (!orden.getUserId().equals(request.getUserId())
                || !orden.getProductId().equals(request.getProductId())) {
            throw new IllegalArgumentException(
                    "La orden no corresponde a este usuario y producto, no se puede reseñar");
        }







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
        log.info("Reseña creada exitosamente. reviewId={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

        Review updated = reviewRepository.save(review);
        return toResponse(updated);
    }

    @Override
    public ReviewResponse moderateReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + id));

        review.setStatus(false); // Disable review
        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Override
    public String deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Cannot delete. Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);

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