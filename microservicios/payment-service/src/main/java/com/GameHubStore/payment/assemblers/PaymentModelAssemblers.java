package com.GameHubStore.payment.assemblers;

import com.GameHubStore.payment.controller.PaymentController;
import com.GameHubStore.payment.model.dto.PaymentResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssemblers implements RepresentationModelAssembler<PaymentResponse, EntityModel<PaymentResponse>> {

    @Override
    public EntityModel<PaymentResponse> toModel(PaymentResponse response) {
        return EntityModel.of(
                response,
                linkTo(methodOn(PaymentController.class).getPaymentById(response.getId())).withSelfRel(),
                linkTo(methodOn(PaymentController.class).getAllPayments()).withRel("all-payments"),
                linkTo(methodOn(PaymentController.class).updateEstado(response.getId(), null)).withRel("update-estado"),
                linkTo(methodOn(PaymentController.class).cancelPayment(response.getId())).withRel("cancel-payment")
        );
    }
}