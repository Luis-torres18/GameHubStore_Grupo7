package com.GameHubStore.order.service.service;

import com.GameHubStore.order.client.*;
import com.GameHubStore.order.exception.OrderNotFoundException;
import com.GameHubStore.order.model.dto.OrderRequest;
import com.GameHubStore.order.model.dto.OrderResponse;
import com.GameHubStore.order.model.entities.Order;
import com.GameHubStore.order.repository.OrderRepository;
import com.GameHubStore.order.service.OrderService;
import feign.FeignException;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private InventoryClient inventoryClient;
    @Mock
    private PromotionClient promotionClient;

    @InjectMocks
    private OrderService orderService;

    private Order orderPrueba;
    private OrderRequest requestPrueba;
    private List<Order> orderList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        this.orderList.clear();

        this.orderPrueba = new Order();
        this.orderPrueba.setId(1L);
        this.orderPrueba.setUserId(10L);
        this.orderPrueba.setTotal(50000.0);
        this.orderPrueba.setStatus("PENDING");
        this.orderPrueba.setCreatedAt(LocalDateTime.now());

        this.requestPrueba = new OrderRequest();
        this.requestPrueba.setUserId(10L);
        this.requestPrueba.setProductId(100L);
        this.requestPrueba.setQuantity(2);
        this.requestPrueba.setTotal(50000.0);

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Order order = new Order();
            order.setId((long) (i + 2));
            order.setUserId((long) faker.number().numberBetween(1, 100));
            order.setTotal(faker.number().randomDouble(2, 10000, 90000));
            order.setStatus("PENDING");
            order.setCreatedAt(LocalDateTime.now());
            this.orderList.add(order);
        }
    }

    @Test
    @DisplayName("Debe crear una orden exitosamente y aplicar descuento si hay promoción")
    public void shouldCreateOrderSuccessfully() {
        // Arrange
        UserResponse userResponse = new UserResponse();
        userResponse.setEstado(true);
        lenient().when(userClient.getUserById(10L)).thenReturn(userResponse);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setEstado(true);
        lenient().when(productClient.getProductById(100L)).thenReturn(List.of(productResponse));

        lenient().when(inventoryClient.reserveStock(100L, 2)).thenReturn(new InventoryClientResponse());

        PromotionResponse promo = new PromotionResponse();
        promo.setIsValid(true);
        promo.setMinAmount(10000.0);
        promo.setDiscountAmount(5000.0);
        lenient().when(promotionClient.getActivePromotions()).thenReturn(List.of(promo));

        // Act
        orderService.createOrder(requestPrueba);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear orden si el usuario no existe")
    public void shouldThrowExceptionWhenUserNotFound() {
        lenient().when(userClient.getUserById(10L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> orderService.createOrder(requestPrueba))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No existe usuario con ID: 10");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear orden si el producto esta inactivo")
    public void shouldThrowExceptionWhenProductIsInactive() {
        UserResponse userResponse = new UserResponse();
        userResponse.setEstado(true);
        lenient().when(userClient.getUserById(10L)).thenReturn(userResponse);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setEstado(false);
        lenient().when(productClient.getProductById(100L)).thenReturn(List.of(productResponse));

        assertThatThrownBy(() -> orderService.createOrder(requestPrueba))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El producto está inactivo: 100");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe listar todas las ordenes")
    public void shouldGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(orderPrueba));

        List<OrderResponse> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener una orden por su id")
    public void shouldGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));

        OrderResponse result = orderService.getOrderById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe listar ordenes por userId")
    public void shouldGetOrdersByUserId() {
        when(orderRepository.findByUserId(10L)).thenReturn(List.of(orderPrueba));

        List<OrderResponse> result = orderService.getOrdersByUserId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Debe actualizar el estado de una orden")
    public void shouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.updateStatus(1L, "SHIPPED");

        assertThat(result.getStatus()).isEqualTo("SHIPPED");
        verify(orderRepository, times(1)).save(orderPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al intentar modificar una orden PAID")
    public void shouldThrowExceptionWhenUpdatingPaidOrder() {
        orderPrueba.setStatus("PAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));

        assertThatThrownBy(() -> orderService.updateStatus(1L, "SHIPPED"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede modificar una orden ya pagada.");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe cancelar una orden exitosamente")
    public void shouldCancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.cancelOrder(1L);

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
        verify(orderRepository, times(1)).save(orderPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al cancelar una orden que ya esta pagada")
    public void shouldThrowExceptionWhenCancelingPaidOrder() {
        orderPrueba.setStatus("PAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede cancelar una orden ya pagada.");

        verify(orderRepository, never()).save(any(Order.class));
    }
}