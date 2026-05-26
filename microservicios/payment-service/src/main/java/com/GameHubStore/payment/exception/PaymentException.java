package com.GameHubStore.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class PaymentException {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(
            PaymentNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentValidationException.class)
    public ResponseEntity<ErrorDetails> handleValidation(
            PaymentValidationException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodValidation(
            MethodArgumentNotValidException ex, WebRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), mensaje, request.getDescription(false)),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobal(
            Exception ex, WebRequest request) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), "Error interno del servidor", request.getDescription(false)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}