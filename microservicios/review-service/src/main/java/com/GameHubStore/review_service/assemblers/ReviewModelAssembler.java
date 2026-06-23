package com.GameHubStore.review_service.assemblers;

import com.GameHubStore.review_service.controller.ReviewControllerV2;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReviewModelAssembler
        implements RepresentationModelAssembler<ReviewResponse, EntityModel<ReviewResponse>> {

    @Override
    public EntityModel<ReviewResponse>toModel(ReviewResponse review) {

        EntityModel<ReviewResponse> model = EntityModel.of(review,

                linkTo(methodOn(ReviewControllerV2.class).getReviewById(review.getId()))
                        .withSelfRel(),

                linkTo(methodOn(ReviewControllerV2.class).findAllReviews())
                        .withRel("reviews"),

                linkTo(methodOn(ReviewControllerV2.class).getReviewsByProduct(review.getProductId()))
                        .withRel("reviewsByProduct"),


                linkTo(methodOn(ReviewControllerV2.class).getReviewsByUser(review.getUserId()))
                        .withRel("reviewsByUser")
        );

        if (Boolean.TRUE.equals(review.getStatus())) {
            model.add(linkTo(methodOn(ReviewControllerV2.class)
                    .moderateReview(review.getId())).withRel("moderate"));
        }

        return model;
    }
}