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
    private UserResponse userPrueba;
    private ProductResponse productPrueba;
    private List<Order> orderList;

    @BeforeEach
    public void setUp() {
        orderList = new ArrayList<>();


        userPrueba = new UserResponse();
        userPrueba.setId(1L);
        userPrueba.setEstado(true);

        productPrueba = new ProductResponse();
        productPrueba.setId(1L);
        productPrueba.setEstado(true);
        productPrueba.setPrecio(50000.0);

        requestPrueba = new OrderRequest();
        requestPrueba.setUserId(1L);
        requestPrueba.setProductId(1L);
        requestPrueba.setQuantity(2);
        requestPrueba.setTotal(50000.0);

        orderPrueba = Order.builder()
                .id(1L)
                .userId(1L)
                .productId(1)
                .quantity(2)
                .status("PENDING")
                .total(50000.0)
                .createdAt(LocalDateTime.now())
                .build();

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Order order = Order.builder()
                    .id((long) (i + 2))
                    .userId((long) faker.number().numberBetween(1, 20))
                    .productId(faker.number().numberBetween(1, 50))
                    .quantity(faker.number().numberBetween(1, 10))
                    .status(faker.options().option("PENDING", "PAID", "CANCELLED"))
                    .total(faker.number().randomDouble(2, 5000, 500000))
                    .createdAt(LocalDateTime.now())
                    .build();
            orderList.add(order);
        }
    }



    @Test
    @DisplayName("Debe crear una orden válida")
    public void shouldCreateOrder() {
        when(userClient.getUserById(1L)).thenReturn(userPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productPrueba));
        when(inventoryClient.reserveStock(1L, 2)).thenReturn(new InventoryClientResponse());
        when(promotionClient.getActivePromotions()).thenReturn(List.of());
        when(orderRepository.save(any(Order.class))).thenReturn(orderPrueba);

        orderService.createOrder(requestPrueba);

        verify(userClient, times(1)).getUserById(1L);
        verify(productClient, times(1)).getProductById(1L);
        verify(inventoryClient, times(1)).reserveStock(1L, 2);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    public void shouldNotCreateOrderWhenUserNotFound() {
        when(userClient.getUserById(1L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> orderService.createOrder(requestPrueba))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No existe usuario con ID: 1");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario está inactivo")
    public void shouldNotCreateOrderWhenUserInactive() {
        userPrueba.setEstado(false);
        when(userClient.getUserById(1L)).thenReturn(userPrueba);

        assertThatThrownBy(() -> orderService.createOrder(requestPrueba))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Usuario inactivo o no encontrado");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no existe")
    public void shouldNotCreateOrderWhenProductNotFound() {
        when(userClient.getUserById(1L)).thenReturn(userPrueba);
        when(productClient.getProductById(1L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> orderService.createOrder(requestPrueba))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No existe producto con ID: 1");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto está inactivo")
    public void shouldNotCreateOrderWhenProductInactive() {
        productPrueba.setEstado(false);
        when(userClient.getUserById(1L)).thenReturn(userPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productPrueba));

        assertThatThrownBy(() -> orderService.createOrder(requestPrueba))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El producto está inactivo");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si no hay stock disponible")
    public void shouldNotCreateOrderWhenNoStock() {
        when(userClient.getUserById(1L)).thenReturn(userPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productPrueba));
        when(inventoryClient.reserveStock(1L, 2)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> orderService.createOrder(requestPrueba))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No existe stock para el producto");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe crear la orden aunque promotion-service no responda")
    public void shouldCreateOrderEvenWhenPromotionFails() {
        when(userClient.getUserById(1L)).thenReturn(userPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productPrueba));
        when(inventoryClient.reserveStock(1L, 2)).thenReturn(new InventoryClientResponse());
        when(promotionClient.getActivePromotions()).thenThrow(mock(FeignException.class));
        when(orderRepository.save(any(Order.class))).thenReturn(orderPrueba);

        orderService.createOrder(requestPrueba);

        verify(orderRepository, times(1)).save(any(Order.class));
    }


    @Test
    @DisplayName("Debe listar todas las órdenes (50 con DataFaker)")
    public void shouldGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(orderList);

        List<OrderResponse> result = orderService.getAllOrders();

        assertThat(result).hasSize(50);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener una orden por su ID")
    public void shouldGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));

        OrderResponse result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar una orden inexistente")
    public void shouldNotGetOrderByIdWhenNotFound() {
        when(orderRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(9999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with ID: 9999");

        verify(orderRepository, times(1)).findById(9999L);
    }

    @Test
    @DisplayName("Debe listar órdenes por userId")
    public void shouldGetOrdersByUserId() {
        when(orderRepository.findByUserId(1L)).thenReturn(orderList);

        List<OrderResponse> result = orderService.getOrdersByUserId(1L);

        assertThat(result).hasSize(50);
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Debe listar órdenes por estado")
    public void shouldGetOrdersByStatus() {
        when(orderRepository.findByStatus("PENDING")).thenReturn(List.of(orderPrueba));

        List<OrderResponse> result = orderService.getOrdersByStatus("PENDING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        verify(orderRepository, times(1)).findByStatus("PENDING");
    }



    @Test
    @DisplayName("Debe actualizar el estado de una orden")
    public void shouldUpdateOrderStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.updateStatus(1L, "PAID");

        assertThat(result.getStatus()).isEqualTo("PAID");
        verify(orderRepository, times(1)).save(orderPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al modificar una orden ya pagada")
    public void shouldNotUpdateStatusWhenAlreadyPaid() {
        orderPrueba.setStatus("PAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));

        assertThatThrownBy(() -> orderService.updateStatus(1L, "CANCELLED"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede modificar una orden ya pagada");

        verify(orderRepository, never()).save(any(Order.class));
    }



    @Test
    @DisplayName("Debe cancelar una orden en estado PENDING")
    public void shouldCancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.cancelOrder(1L);

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
        verify(orderRepository, times(1)).save(orderPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar una orden ya cancelada")
    public void shouldNotCancelAlreadyCancelledOrder() {
        orderPrueba.setStatus("CANCELLED");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("La orden ya está cancelada");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar una orden ya pagada")
    public void shouldNotCancelPaidOrder() {
        orderPrueba.setStatus("PAID");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderPrueba));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede cancelar una orden ya pagada");

        verify(orderRepository, never()).save(any(Order.class));
    }
}