package com.GameHubStore.inventory.assemblers;

import com.GameHubStore.inventory.controller.InventoryController;
import com.GameHubStore.inventory.model.dto.InventoryResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventoryModelAssemblers implements RepresentationModelAssembler<InventoryResponse, EntityModel<InventoryResponse>> {

    @Override
    public EntityModel<InventoryResponse> toModel(InventoryResponse response) {
        return EntityModel.of(
                response,
                linkTo(methodOn(InventoryController.class).getInventoryByProductId(response.getProductId())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).getInventoryByLocation(response.getLocation())).withRel("by-location"),
                linkTo(methodOn(InventoryController.class).updateStock(response.getId(), null, null)).withRel("update-stock"),
                linkTo(methodOn(InventoryController.class).reserveStock(response.getProductId(), null)).withRel("reserve-stock")
        );
    }
}