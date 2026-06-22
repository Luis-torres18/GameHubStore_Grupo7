package com.GameHubStore.shipping.assemblers;

import com.GameHubStore.shipping.controller.ShippingControllerV2;
import com.GameHubStore.shipping.model.dto.ShippingResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ShippingModelAssemblers implements RepresentationModelAssembler<ShippingResponse, EntityModel<ShippingResponse>> {
    @Override
    public EntityModel<ShippingResponse> toModel (ShippingResponse response) {
        return EntityModel.of(
                response,
                linkTo(methodOn(ShippingControllerV2.class).getShippingById(response.getId())).withSelfRel(),
                linkTo(methodOn(ShippingControllerV2.class).getShippings(null, null)).withRel("all-shippings"),
                linkTo(methodOn(ShippingControllerV2.class).updateShippingStatus(response.getId(), null, null)).withRel("update-status"),
                linkTo(methodOn(ShippingControllerV2.class).cancelShipping(response.getId())).withRel("cancel-shipping")
        );
    }
}
