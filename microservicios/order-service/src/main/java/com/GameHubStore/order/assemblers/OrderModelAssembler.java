package com.GameHubStore.order.assemblers;

import com.GameHubStore.order.controller.OrderControllerV2;
import com.GameHubStore.order.model.dto.OrderResponse;
import com.GameHubStore.order.model.entities.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler  implements RepresentationModelAssembler<OrderResponse, EntityModel<OrderResponse>> {
    @Override
    public EntityModel<OrderResponse> toModel(OrderResponse response) {
        return EntityModel.of(
                response,
                linkTo(methodOn(OrderControllerV2.class).getOrderById(response.getId())).withSelfRel(),
                linkTo(methodOn(OrderControllerV2.class).getAllOrders()).withRel("all-orders"),
                linkTo(methodOn(OrderControllerV2.class).updateStatus(response.getId(), null)).withRel("update-status"),
                linkTo(methodOn(OrderControllerV2.class).cancelOrder(response.getId())).withRel("cancel-order")
        );
 }
}
