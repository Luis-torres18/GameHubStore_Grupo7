package com.GameHubStore.order.controller;

import jakarta.persistence.criteria.Order;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Orden> createOrder(@Valid @RequestBody OrderRequest request) {
        Orden newOrder = orderService.createOrder(request);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(){
        return ResponseEntity.ok(orderService.getAllorders());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id){
        return ResponseEntity.ok(orderService.getOrderById(id))
    }
}
