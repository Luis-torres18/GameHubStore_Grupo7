package com.GameHubStore.shipping.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShippingValidationException extends RuntimeException {

    public ShippingValidationException(String message) {
        super(message);
    }
}