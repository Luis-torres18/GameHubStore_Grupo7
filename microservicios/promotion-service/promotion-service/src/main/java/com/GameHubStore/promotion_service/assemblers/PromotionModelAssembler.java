package com.GameHubStore.promotion_service.assemblers;

import com.GameHubStore.promotion_service.controller.PromotionControllerV2;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * HATEOAS assembler for {@link PromotionResponse}.
 * <p>Transforms a {@code PromotionResponse} into an {@link EntityModel} by adding
 * navigation links (self and collection) exposed by the v2 API.</p>
 */

// @Component: registers this class as a bean so it can be injected into ControllerV2.
// RepresentationModelAssembler<PromotionResponse, EntityModel<PromotionResponse>>:
// converts a PromotionResponse (DTO) into an EntityModel (DTO + HATEOAS links),
// all in one reusable place.
@Component
public class PromotionModelAssembler
        implements RepresentationModelAssembler<PromotionResponse, EntityModel<PromotionResponse>> {

    /**
     * Builds the HATEOAS representation of a promotion.
     *
     * @param promotion the DTO to represent
     * @return model with the promotion and {@code self}, {@code promotions},
     *         {@code activePromotions} and {@code promotionByCode} links
     */
    @Override
    public EntityModel<PromotionResponse> toModel(PromotionResponse promotion) {

        // EntityModel.of(data, ...links) wraps the promotion together with its links.
        EntityModel<PromotionResponse> model = EntityModel.of(
                promotion,

                // self: link to the resource itself (GET /api/v2/promotion/{id}).
                // linkTo + methodOn build the URL by reading the method mapping,
                // without hardcoding it.
                linkTo(methodOn(PromotionControllerV2.class)
                        .getPromotionById(promotion.getId())).withSelfRel(),

                // promotions: link to the full collection so the client knows how to navigate.
                linkTo(methodOn(PromotionControllerV2.class)
                        .getAllPromotions()).withRel("promotions"),

                // activePromotions: link to filter only active promotions.
                linkTo(methodOn(PromotionControllerV2.class)
                        .getActivePromotions()).withRel("activePromotions"),

                // promotionByCode: link to look up this promotion by its unique code.
                linkTo(methodOn(PromotionControllerV2.class)
                        .getPromotionByCode(promotion.getCode())).withRel("promotionByCode")
        );

        // desactivate link is only offered when the promotion is still active —
        // if it is already inactive, that action makes no sense (true HATEOAS behaviour).
        if (Boolean.TRUE.equals(promotion.getIsActive())) {
            model.add(linkTo(methodOn(PromotionControllerV2.class)
                    .desactivatePromotion(promotion.getId())).withRel("desactivate"));
        }

        return model;
    }
}