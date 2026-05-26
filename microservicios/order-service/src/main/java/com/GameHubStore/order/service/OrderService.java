package com.GameHubStore.order.service;

import com.GameHubStore.order.exception.OrderNotFoundException;
import com.GameHubStore.order.model.dto.OrderRequest;
import com.GameHubStore.order.repository.OrderRepository;
import com.GameHubStore.order.model.entities.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // Method to create a new order
    public Order createOrder(OrderRequest request) {
        Order newOrder = Order.builder()
                .userId(request.getUserId())
                .total(request.getTotal())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        return orderRepository.save(newOrder);
    }

    // Method to get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Method to get a specific order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }
}