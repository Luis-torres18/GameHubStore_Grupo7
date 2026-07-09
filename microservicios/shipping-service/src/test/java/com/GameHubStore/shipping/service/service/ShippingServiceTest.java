package com.GameHubStore.shipping.service.service;

import com.GameHubStore.shipping.client.OrderClient;
import com.GameHubStore.shipping.client.Userclient;
import com.GameHubStore.shipping.exception.ShippingNotFoundException;
import com.GameHubStore.shipping.exception.ShippingValidationException;
import com.GameHubStore.shipping.model.dto.ShippingRequest;
import com.GameHubStore.shipping.model.dto.ShippingResponse;
import com.GameHubStore.shipping.model.entities.Shipping;
import com.GameHubStore.shipping.repository.ShippingRepository;
import com.GameHubStore.shipping.service.ShippingService;
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
public class ShippingServiceTest {

    @Mock
    private ShippingRepository shippingRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private Userclient userClient;

    @InjectMocks
    private ShippingService shippingService;

    private Shipping shippingPrueba;
    private ShippingRequest requestPrueba;
    private List<Shipping> shippingList;

    @BeforeEach
    public void setUp() {
        shippingList = new ArrayList<>();

        requestPrueba = new ShippingRequest();
        requestPrueba.setOrdenId(1L);
        requestPrueba.setUsuarioId(1L);
        requestPrueba.setDireccion("Av. Providencia 123, Santiago");
        requestPrueba.setTransportista("Chilexpress");
        requestPrueba.setEstadoOrden("PAID");

        shippingPrueba = Shipping.builder()
                .id(1L)
                .ordenId(1L)
                .usuarioId(1L)
                .direccion("Av. Providencia 123, Santiago")
                .transportista("Chilexpress")
                .estado("PENDIENTE")
                .tracking("TRK-UUID-001")
                .fechaEnvio(LocalDateTime.now())
                .build();

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Shipping shipping = Shipping.builder()
                    .id((long) (i + 2))
                    .ordenId((long) faker.number().numberBetween(1, 100))
                    .usuarioId((long) faker.number().numberBetween(1, 50))
                    .direccion(faker.address().fullAddress())
                    .transportista(faker.options().option("Chilexpress", "Starken", "Correos Chile"))
                    .estado(faker.options().option("PENDIENTE", "EN_CAMINO", "ENTREGADO"))
                    .tracking("TRK-" + faker.number().digits(8))
                    .fechaEnvio(LocalDateTime.now())
                    .build();
            shippingList.add(shipping);
        }
    }


    @Test
    @DisplayName("Debe crear un despacho válido con orden PAID")
    public void shouldCreateShipping() {
        when(orderClient.getOrderById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenReturn(new Object());
        when(shippingRepository.save(any(Shipping.class))).thenReturn(shippingPrueba);

        ShippingResponse result = shippingService.createShipping(requestPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getOrdenId()).isEqualTo(1L);
        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
        assertThat(result.getTracking()).isNotBlank();
        verify(orderClient, times(1)).getOrderById(1L);
        verify(userClient, times(1)).getUserById(1L);
        verify(shippingRepository, times(1)).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no existe en order-service")
    public void shouldNotCreateShippingWhenOrderNotFound() {
        when(orderClient.getOrderById(1L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> shippingService.createShipping(requestPrueba))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("Order not found with ID: 1");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si order-service no responde")
    public void shouldNotCreateShippingWhenOrderServiceUnavailable() {
        when(orderClient.getOrderById(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> shippingService.createShipping(requestPrueba))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("Could not validate order");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe en user-service")
    public void shouldNotCreateShippingWhenUserNotFound() {
        when(orderClient.getOrderById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> shippingService.createShipping(requestPrueba))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("User not found with ID: 1");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si user-service no responde")
    public void shouldNotCreateShippingWhenUserServiceUnavailable() {
        when(orderClient.getOrderById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> shippingService.createShipping(requestPrueba))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("Could not validate user");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no está pagada")
    public void shouldNotCreateShippingWhenOrderNotPaid() {
        requestPrueba.setEstadoOrden("PENDING");
        when(orderClient.getOrderById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenReturn(new Object());

        assertThatThrownBy(() -> shippingService.createShipping(requestPrueba))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("Only paid orders can be shipped");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la dirección está vacía")
    public void shouldNotCreateShippingWhenAddressBlank() {
        requestPrueba.setDireccion("  ");
        when(orderClient.getOrderById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenReturn(new Object());

        assertThatThrownBy(() -> shippingService.createShipping(requestPrueba))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("A valid address is required");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la dirección es nula")
    public void shouldNotCreateShippingWhenAddressNull() {
        requestPrueba.setDireccion(null);
        when(orderClient.getOrderById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenReturn(new Object());

        assertThatThrownBy(() -> shippingService.createShipping(requestPrueba))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("A valid address is required");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    // ══════════════════════════════════════════════════════════════
    // ─── getShippings ─────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe listar todos los despachos (50 con DataFaker)")
    public void shouldGetAllShippings() {
        when(shippingRepository.findAll()).thenReturn(shippingList);

        List<ShippingResponse> result = shippingService.getShippings(null, null);

        assertThat(result).hasSize(50);
        verify(shippingRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar despachos por ordenId")
    public void shouldGetShippingsByOrdenId() {
        when(shippingRepository.findByOrdenId(1L)).thenReturn(List.of(shippingPrueba));

        List<ShippingResponse> result = shippingService.getShippings(1L, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrdenId()).isEqualTo(1L);
        verify(shippingRepository, times(1)).findByOrdenId(1L);
    }

    @Test
    @DisplayName("Debe listar despachos por estado")
    public void shouldGetShippingsByEstado() {
        when(shippingRepository.findByEstado("PENDIENTE")).thenReturn(List.of(shippingPrueba));

        List<ShippingResponse> result = shippingService.getShippings(null, "PENDIENTE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo("PENDIENTE");
        verify(shippingRepository, times(1)).findByEstado("PENDIENTE");
    }

    @Test
    @DisplayName("Debe obtener un despacho por su ID")
    public void shouldGetShippingById() {
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shippingPrueba));

        ShippingResponse result = shippingService.getShippingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTransportista()).isEqualTo("Chilexpress");
        verify(shippingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un despacho inexistente")
    public void shouldNotGetShippingByIdWhenNotFound() {
        when(shippingRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.getShippingById(9999L))
                .isInstanceOf(ShippingNotFoundException.class)
                .hasMessageContaining("Shipping not found with ID: 9999");

        verify(shippingRepository, times(1)).findById(9999L);
    }

    // ══════════════════════════════════════════════════════════════
    // ─── updateShippingStatus ─────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe actualizar el estado del despacho")
    public void shouldUpdateShippingStatus() {
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shippingPrueba));
        when(shippingRepository.save(any(Shipping.class))).thenAnswer(inv -> inv.getArgument(0));

        ShippingResponse result = shippingService.updateShippingStatus(1L, "EN_CAMINO", null);

        assertThat(result.getEstado()).isEqualTo("EN_CAMINO");
        verify(shippingRepository, times(1)).save(shippingPrueba);
    }

    @Test
    @DisplayName("Debe registrar fechaEntrega al cambiar a ENTREGADO")
    public void shouldSetFechaEntregaWhenEntregado() {
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shippingPrueba));
        when(shippingRepository.save(any(Shipping.class))).thenAnswer(inv -> inv.getArgument(0));

        ShippingResponse result = shippingService.updateShippingStatus(1L, "ENTREGADO", null);

        assertThat(result.getEstado()).isEqualTo("ENTREGADO");
        assertThat(shippingPrueba.getFechaEntrega()).isNotNull();
        verify(shippingRepository, times(1)).save(shippingPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar a ENTREGADO sin fechaEnvio")
    public void shouldNotUpdateToEntregadoWithoutFechaEnvio() {
        shippingPrueba.setFechaEnvio(null);
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shippingPrueba));

        assertThatThrownBy(() -> shippingService.updateShippingStatus(1L, "ENTREGADO", null))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("Cannot set ENTREGADO without a valid shipping date");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al usar tracking duplicado")
    public void shouldNotUpdateWithDuplicateTracking() {
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shippingPrueba));
        when(shippingRepository.existsByTracking("TRK-DUPLICADO")).thenReturn(true);

        assertThatThrownBy(() -> shippingService.updateShippingStatus(1L, "EN_CAMINO", "TRK-DUPLICADO"))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("Tracking number already exists");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un despacho inexistente")
    public void shouldNotUpdateShippingWhenNotFound() {
        when(shippingRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.updateShippingStatus(9999L, "EN_CAMINO", null))
                .isInstanceOf(ShippingNotFoundException.class)
                .hasMessageContaining("Shipping not found with ID: 9999");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    // ══════════════════════════════════════════════════════════════
    // ─── cancelShipping ───────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe cancelar un despacho en estado PENDIENTE")
    public void shouldCancelShipping() {
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shippingPrueba));
        when(shippingRepository.save(any(Shipping.class))).thenAnswer(inv -> inv.getArgument(0));

        shippingService.cancelShipping(1L);

        assertThat(shippingPrueba.getEstado()).isEqualTo("CANCELADO");
        verify(shippingRepository, times(1)).save(shippingPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar un despacho ya entregado")
    public void shouldNotCancelDeliveredShipping() {
        shippingPrueba.setEstado("ENTREGADO");
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shippingPrueba));

        assertThatThrownBy(() -> shippingService.cancelShipping(1L))
                .isInstanceOf(ShippingValidationException.class)
                .hasMessageContaining("Cannot cancel a shipping that has already been delivered");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar un despacho inexistente")
    public void shouldNotCancelShippingWhenNotFound() {
        when(shippingRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.cancelShipping(9999L))
                .isInstanceOf(ShippingNotFoundException.class)
                .hasMessageContaining("Shipping not found with ID: 9999");

        verify(shippingRepository, never()).save(any(Shipping.class));
    }
}
