package com.GameHubStore.warranty_service.assemblers;

import com.GameHubStore.warranty_service.controller.WarrantyControllerV2;
import com.GameHubStore.warranty_service.model.dto.WarrantyResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class WarrantyModelAssembler
        implements RepresentationModelAssembler<WarrantyResponse, EntityModel<WarrantyResponse>> {

    @Override
    public EntityModel<WarrantyResponse>toModel(WarrantyResponse warranty) {

        EntityModel<WarrantyResponse> model = EntityModel.of(warranty,
                linkTo(methodOn(WarrantyControllerV2.class).findById(warranty.getId()))
                        .withSelfRel(),

                linkTo(methodOn(WarrantyControllerV2.class).findAllWarranties())
                        .withRel("warranties"),

                linkTo(methodOn(WarrantyControllerV2.class).findByUser(warranty.getUserId()))
                        .withRel("warrantiesByUser"),

                linkTo(methodOn(WarrantyControllerV2.class).findByProduct(warranty.getProductId()))
                        .withRel("warrantiesByProduct"),

                linkTo(methodOn(WarrantyControllerV2.class).findByStatus(warranty.getStatus()))
                        .withRel("warrantiesByStatus")
        );

        if (!"CLOSED".equalsIgnoreCase(warranty.getStatus())) {
            model.add(
                    linkTo(methodOn(WarrantyControllerV2.class)
                            .updateWarranty(warranty.getId(), null)).withRel("update"),
                    linkTo(methodOn(WarrantyControllerV2.class)
                            .closeWarranty(warranty.getId(), null)).withRel("close")
            );
        }

        return model;
    }
}