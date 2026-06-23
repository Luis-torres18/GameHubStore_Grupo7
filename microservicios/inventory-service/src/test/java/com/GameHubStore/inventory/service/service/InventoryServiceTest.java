package com.GameHubStore.inventory.service.service;

import com.GameHubStore.inventory.client.ProductClient;
import com.GameHubStore.inventory.exception.BusinessException;
import com.GameHubStore.inventory.exception.InventoryNotFoundException;
import com.GameHubStore.inventory.model.dto.InventoryRequest;
import com.GameHubStore.inventory.model.dto.InventoryResponse;
import com.GameHubStore.inventory.model.entities.Inventory;
import com.GameHubStore.inventory.model.entities.InventoryMovement;
import com.GameHubStore.inventory.repository.InventoryMovementRepository;
import com.GameHubStore.inventory.repository.InventoryRepository;
import com.GameHubStore.inventory.service.InventoryService;
import feign.FeignException;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventoryPrueba;
    private InventoryRequest requestPrueba;
    private final List<Inventory> inventoryList = new ArrayList<>();


    @BeforeEach
    public void setUp() {
        this.inventoryPrueba = Inventory.builder()
                .id(1L)
                .productId(100L)
                .availableStock(50)
                .reservedStock(10)
                .minimumStock(10)
                .Location("Bodega Central")
                .build();

        this.requestPrueba = InventoryRequest.builder()
                .productId(100L)
                .availableStock(50)
                .minimumStock(10)
                .Location("Bodega Central")
                .build();

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Inventory inv = Inventory.builder()
                    .id((long) (i + 2))
                    .productId(faker.number().numberBetween(1, 1000))
                    .availableStock(faker.number().numberBetween(20, 200))
                    .reservedStock(faker.number().numberBetween(0, 10))
                    .minimumStock(faker.number().numberBetween(5, 15))
                    .Location("Bodega " + faker.address().cityName())
                    .build();
            this.inventoryList.add(inv);
        }
    }


    @Test
    @DisplayName("Debe crear un nuevo registro de inventario y movimiento")
    public void shouldAddInventory() {
        when(this.productClient.getProductById(100L)).thenReturn(List.of());
        when(this.inventoryRepository.existsByProductId(100L)).thenReturn(false);
        when(this.inventoryRepository.save(any(Inventory.class))).thenReturn(this.inventoryPrueba);

        InventoryResponse result = this.inventoryService.addInventory(this.requestPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(100L);
        assertThat(result.getAvailableStock()).isEqualTo(50);
        verify(this.inventoryRepository, times(1)).save(any(Inventory.class));
        verify(this.inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
    }


    @Test
    @DisplayName("Debe lanzar excepcion si producto no existe en product-service")
    public void shouldNotAddInventoryWhenProductNotExists() {
        when(this.productClient.getProductById(100L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> this.inventoryService.addInventory(this.requestPrueba))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Product not found in product-service with id=100");
        verify(this.inventoryRepository, never()).save(any(Inventory.class));
    }


    @Test
    @DisplayName("Debe lanzar excepcion si el inventario ya existe para el producto")
    public void shouldNotAddInventoryWhenAlreadyExists() {
        when(this.productClient.getProductById(100L)).thenReturn(List.of());
        when(this.inventoryRepository.existsByProductId(100L)).thenReturn(true);

        assertThatThrownBy(() -> this.inventoryService.addInventory(this.requestPrueba))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock record already exists for productId=100");
        verify(this.inventoryRepository, never()).save(any(Inventory.class));
    }


    @Test
    @DisplayName("Debe obtener inventario por productId")
    public void shouldGetInventoryByProductId() {
        when(this.inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(this.inventoryPrueba));

        InventoryResponse result = this.inventoryService.getInventoryByProductId(100L);

        assertThat(result.getProductId()).isEqualTo(100L);
        verify(this.inventoryRepository, times(1)).findByProductId(100L);
    }


    @Test
    @DisplayName("Debe lanzar excepcion al buscar por productId inexistente")
    public void shouldNotGetInventoryByProductIdWhenNotFound() {
        when(this.inventoryRepository.findByProductId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.inventoryService.getInventoryByProductId(999L))
                .isInstanceOf(InventoryNotFoundException.class)
                .hasMessageContaining("Stock not found for productId=999");
    }

    @Test
    @DisplayName("Debe listar inventario por location")
    public void shouldGetInventoryByLocation() {
        when(this.inventoryRepository.findByLocation("Bodega Central")).thenReturn(List.of(this.inventoryPrueba));

        List<InventoryResponse> result = this.inventoryService.getInventoryByLocation("Bodega Central");

        assertThat(result).hasSize(1);
        verify(this.inventoryRepository, times(1)).findByLocation("Bodega Central");
    }

    @Test
    @DisplayName("Debe obtener inventario por id")
    public void shouldGetInventoryById() {
        when(this.inventoryRepository.findById(1L)).thenReturn(Optional.of(this.inventoryPrueba));

        InventoryResponse result = this.inventoryService.getInventoryById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(this.inventoryRepository, times(1)).findById(1L);
    }


    @Test
    @DisplayName("Debe actualizar el stock exitosamente")
    public void shouldUpdateStock() {
        when(this.inventoryRepository.findById(1L)).thenReturn(Optional.of(this.inventoryPrueba));
        when(this.inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryResponse result = this.inventoryService.updateStock(1L, 100, 20);

        assertThat(result.getAvailableStock()).isEqualTo(100);
        assertThat(result.getReservedStock()).isEqualTo(20);
        verify(this.inventoryRepository, times(1)).save(any(Inventory.class));
        verify(this.inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
    }


    @Test
    @DisplayName("Debe lanzar excepcion al actualizar con stock negativo")
    public void shouldNotUpdateStockWhenNegative() {
        when(this.inventoryRepository.findById(1L)).thenReturn(Optional.of(this.inventoryPrueba));

        assertThatThrownBy(() -> this.inventoryService.updateStock(1L, -5, 10))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock values cannot be negative");
        verify(this.inventoryRepository, never()).save(any(Inventory.class));
    }


    @Test
    @DisplayName("Debe lanzar excepcion al actualizar reservando mas de lo disponible")
    public void shouldNotUpdateStockWhenReservedExceedsAvailable() {
        when(this.inventoryRepository.findById(1L)).thenReturn(Optional.of(this.inventoryPrueba));

        assertThatThrownBy(() -> this.inventoryService.updateStock(1L, 50, 60))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Reserved stock cannot exceed available stock");
        verify(this.inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe reservar stock exitosamente")
    public void shouldReserveStock() {
        when(this.inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(this.inventoryPrueba));
        when(this.inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryResponse result = this.inventoryService.reserveStock(100L, 10);

        assertThat(result.getReservedStock()).isEqualTo(20); // 10 original + 10 nuevos
        verify(this.inventoryRepository, times(1)).save(any(Inventory.class));
        verify(this.inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al reservar por falta de stock libre")
    public void shouldNotReserveStockWhenInsufficient() {
        when(this.inventoryRepository.findByProductId(100L)).thenReturn(Optional.of(this.inventoryPrueba));


        assertThatThrownBy(() -> this.inventoryService.reserveStock(100L, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");
        verify(this.inventoryRepository, never()).save(any(Inventory.class));
    }


    @Test
    @DisplayName("Debe eliminar un inventario por id")
    public void shouldDeleteInventory() {
        when(this.inventoryRepository.findById(1L)).thenReturn(Optional.of(this.inventoryPrueba));

        this.inventoryService.deleteInventory(1L);

        verify(this.inventoryRepository, times(1)).delete(this.inventoryPrueba);
    }
}