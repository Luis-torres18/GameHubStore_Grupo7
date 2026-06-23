package com.GameHubStore.warranty_service.Service;

import com.GameHubStore.warranty_service.client.OrderClient;
import com.GameHubStore.warranty_service.client.ProductClient;
import com.GameHubStore.warranty_service.client.UserClient;
import com.GameHubStore.warranty_service.client.dto.OrderDto;
import com.GameHubStore.warranty_service.client.dto.ProductDto;
import com.GameHubStore.warranty_service.client.dto.UserDto;
import com.GameHubStore.warranty_service.exception.WarrantyInvalidException;
import com.GameHubStore.warranty_service.exception.WarrantyNotFoundException;
import com.GameHubStore.warranty_service.model.dto.CloseWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.UpdateWarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyRequest;
import com.GameHubStore.warranty_service.model.dto.WarrantyResponse;
import com.GameHubStore.warranty_service.model.entities.Warranty;
import com.GameHubStore.warranty_service.repository.WarrantyRepository;
import com.GameHubStore.warranty_service.service.WarrantyServiceImpl;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarrantyServiceTest {

    @Mock
    private WarrantyRepository warrantyRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private WarrantyServiceImpl warrantyService;

    private Warranty warrantyPrueba;
    private WarrantyRequest requestPrueba;
    private UserDto usuarioPrueba;
    private ProductDto productoPrueba;
    private OrderDto ordenPrueba;
    private List<Warranty> warrantyList;

    @BeforeEach
    public void setUp() {
        warrantyList = new ArrayList<>();

        usuarioPrueba = new UserDto();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setEstado(true);

        productoPrueba = new ProductDto();
        productoPrueba.setId(1L);

        ordenPrueba = new OrderDto();
        ordenPrueba.setId(1L);
        ordenPrueba.setUserId(1L);
        ordenPrueba.setProductId(1L);

        requestPrueba = new WarrantyRequest();
        requestPrueba.setUserId(1L);
        requestPrueba.setProductId(1L);
        requestPrueba.setOrderId(1L);
        requestPrueba.setReason("Producto defectuoso");

        warrantyPrueba = Warranty.builder()
                .id(1L)
                .userId(1L)
                .orderId(1L)
                .productId(1L)
                .reason("Producto defectuoso")
                .status("PENDING")
                .requestDate(LocalDate.now())
                .build();

        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Warranty warranty = Warranty.builder()
                    .id((long) (i + 2))
                    .userId((long) faker.number().numberBetween(1, 20))
                    .orderId((long) faker.number().numberBetween(1, 100))
                    .productId((long) faker.number().numberBetween(1, 50))
                    .reason(faker.lorem().sentence())
                    .status(faker.options().option("PENDING", "IN_REVIEW", "CLOSED"))
                    .requestDate(LocalDate.now())
                    .build();
            warrantyList.add(warranty);
        }

        System.out.println("==========================================");
        System.out.println("  Datos de prueba inicializados correctamente");
        System.out.println("  - Usuario ID: " + usuarioPrueba.getId() + " | Estado: " + usuarioPrueba.getEstado());
        System.out.println("  - Producto ID: " + productoPrueba.getId());
        System.out.println("  - Orden ID: " + ordenPrueba.getId());
        System.out.println("  - Garantías aleatorias generadas: " + warrantyList.size());
        System.out.println("==========================================");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── createWarranty ───────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe crear una garantía válida")
    public void shouldCreateWarranty() {
        System.out.println("\n[TEST] Debe crear una garantía válida");
        System.out.println("  → Entrada: userId=1, productId=1, orderId=1, motivo='Producto defectuoso'");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productoPrueba));
        when(orderClient.getOrderById(1L)).thenReturn(List.of(ordenPrueba));
        when(warrantyRepository.save(any(Warranty.class))).thenReturn(warrantyPrueba);

        WarrantyResponse result = warrantyService.createWarranty(requestPrueba);

        System.out.println("  → Resultado: id=" + result.getId() + " | estado=" + result.getStatus() + " | userId=" + result.getUserId());
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(warrantyRepository, times(1)).save(any(Warranty.class));
        System.out.println("  ✓ Garantía creada correctamente con estado PENDING");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    public void shouldNotCreateWarrantyWhenUserNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción si el usuario no existe");
        System.out.println("  → Simulando userClient.getUserById(1L) retorna null");

        when(userClient.getUserById(1L)).thenReturn(null);

        assertThatThrownBy(() -> warrantyService.createWarranty(requestPrueba))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("no existe");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — usuario no existe");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario está inactivo")
    public void shouldNotCreateWarrantyWhenUserInactive() {
        System.out.println("\n[TEST] Debe lanzar excepción si el usuario está inactivo");
        usuarioPrueba.setEstado(false);
        System.out.println("  → Simulando usuario ID=1 con estado=false (inactivo)");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);

        assertThatThrownBy(() -> warrantyService.createWarranty(requestPrueba))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("inactivo");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — usuario inactivo");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no existe")
    public void shouldNotCreateWarrantyWhenProductNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción si el producto no existe");
        System.out.println("  → Simulando productClient.getProductById(1L) retorna lista vacía");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> warrantyService.createWarranty(requestPrueba))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("no existe");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — producto no existe");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no existe")
    public void shouldNotCreateWarrantyWhenOrderNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción si la orden no existe");
        System.out.println("  → Simulando orderClient.getOrderById(1L) retorna lista vacía");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productoPrueba));
        when(orderClient.getOrderById(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> warrantyService.createWarranty(requestPrueba))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("no existe");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — orden no existe");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no corresponde al usuario y producto")
    public void shouldNotCreateWarrantyWhenOrderMismatch() {
        System.out.println("\n[TEST] Debe lanzar excepción si la orden no corresponde al usuario y producto");
        ordenPrueba.setUserId(99L);
        System.out.println("  → Simulando orden con userId=99 (distinto al usuario del request userId=1)");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productoPrueba));
        when(orderClient.getOrderById(1L)).thenReturn(List.of(ordenPrueba));

        assertThatThrownBy(() -> warrantyService.createWarranty(requestPrueba))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("no corresponde");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — orden no corresponde al usuario");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── GET ──────────────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe listar todas las garantías (50 con DataFaker)")
    public void shouldFindAllWarranties() {
        System.out.println("\n[TEST] Debe listar todas las garantías");
        System.out.println("  → Simulando repositorio con " + warrantyList.size() + " garantías generadas con DataFaker");

        when(warrantyRepository.findAll()).thenReturn(warrantyList);

        List<WarrantyResponse> result = warrantyService.findAll();

        System.out.println("  → Resultado: " + result.size() + " garantías obtenidas");
        assertThat(result).hasSize(50);
        verify(warrantyRepository, times(1)).findAll();
        System.out.println("  ✓ Lista retornada correctamente con 50 garantías");
    }

    @Test
    @DisplayName("Debe obtener una garantía por su ID")
    public void shouldFindWarrantyById() {
        System.out.println("\n[TEST] Debe obtener una garantía por su ID");
        System.out.println("  → Buscando garantía con ID=1");

        when(warrantyRepository.findById(1L)).thenReturn(Optional.of(warrantyPrueba));

        WarrantyResponse result = warrantyService.findById(1L);

        System.out.println("  → Resultado: id=" + result.getId() + " | motivo='" + result.getReason() + "' | estado=" + result.getStatus());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getReason()).isEqualTo("Producto defectuoso");
        verify(warrantyRepository, times(1)).findById(1L);
        System.out.println("  ✓ Garantía encontrada correctamente");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar una garantía inexistente")
    public void shouldNotFindWarrantyByIdWhenNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción al buscar una garantía inexistente");
        System.out.println("  → Buscando garantía con ID=9999 (no existe en el repositorio)");

        when(warrantyRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warrantyService.findById(9999L))
                .isInstanceOf(WarrantyNotFoundException.class)
                .hasMessageContaining("Garantía no encontrada con ID: 9999");

        verify(warrantyRepository, times(1)).findById(9999L);
        System.out.println("  ✓ Excepción WarrantyNotFoundException lanzada correctamente — ID 9999 no existe");
    }

    @Test
    @DisplayName("Debe listar garantías por userId")
    public void shouldFindWarrantiesByUser() {
        System.out.println("\n[TEST] Debe listar garantías por userId");
        System.out.println("  → Buscando garantías del usuario ID=1 (retorna lista de 50)");

        when(warrantyRepository.findByUserId(1L)).thenReturn(warrantyList);

        List<WarrantyResponse> result = warrantyService.findByUser(1L);

        System.out.println("  → Resultado: " + result.size() + " garantías encontradas para userId=1");
        assertThat(result).hasSize(50);
        verify(warrantyRepository, times(1)).findByUserId(1L);
        System.out.println("  ✓ Lista retornada correctamente");
    }

    @Test
    @DisplayName("Debe listar garantías por productId")
    public void shouldFindWarrantiesByProduct() {
        System.out.println("\n[TEST] Debe listar garantías por productId");
        System.out.println("  → Buscando garantías del producto ID=1");

        when(warrantyRepository.findByProductId(1L)).thenReturn(List.of(warrantyPrueba));

        List<WarrantyResponse> result = warrantyService.findByProduct(1L);

        System.out.println("  → Resultado: " + result.size() + " garantía(s) encontrada(s) para productId=1");
        assertThat(result).hasSize(1);
        verify(warrantyRepository, times(1)).findByProductId(1L);
        System.out.println("  ✓ Garantía retornada correctamente");
    }

    @Test
    @DisplayName("Debe listar garantías por estado")
    public void shouldFindWarrantiesByStatus() {
        System.out.println("\n[TEST] Debe listar garantías por estado");
        System.out.println("  → Buscando garantías con estado='PENDING'");

        when(warrantyRepository.findByStatus("PENDING")).thenReturn(List.of(warrantyPrueba));

        List<WarrantyResponse> result = warrantyService.findByStatus("PENDING");

        System.out.println("  → Resultado: " + result.size() + " garantía(s) con estado=" + result.get(0).getStatus());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        verify(warrantyRepository, times(1)).findByStatus("PENDING");
        System.out.println("  ✓ Filtro por estado funcionando correctamente");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── updateWarranty ───────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe actualizar el estado y diagnóstico de una garantía")
    public void shouldUpdateWarranty() {
        System.out.println("\n[TEST] Debe actualizar el estado y diagnóstico de una garantía");
        System.out.println("  → Actualizando garantía ID=1 | nuevo estado='IN_REVIEW' | diagnóstico='Falla en la pantalla'");

        UpdateWarrantyRequest updateRequest = new UpdateWarrantyRequest();
        updateRequest.setStatus("IN_REVIEW");
        updateRequest.setDiagnosis("Falla en la pantalla");

        when(warrantyRepository.findById(1L)).thenReturn(Optional.of(warrantyPrueba));
        when(warrantyRepository.save(any(Warranty.class))).thenAnswer(inv -> inv.getArgument(0));

        WarrantyResponse result = warrantyService.updateWarranty(1L, updateRequest);

        System.out.println("  → Resultado: estado=" + result.getStatus() + " | diagnóstico='" + result.getDiagnosis() + "'");
        assertThat(result.getStatus()).isEqualTo("IN_REVIEW");
        assertThat(result.getDiagnosis()).isEqualTo("Falla en la pantalla");
        verify(warrantyRepository, times(1)).save(warrantyPrueba);
        System.out.println("  ✓ Garantía actualizada correctamente");
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar una garantía ya cerrada")
    public void shouldNotUpdateClosedWarranty() {
        System.out.println("\n[TEST] Debe lanzar excepción al actualizar una garantía ya cerrada");
        warrantyPrueba.setStatus("CLOSED");
        System.out.println("  → Intentando actualizar garantía ID=1 con estado=CLOSED");

        UpdateWarrantyRequest updateRequest = new UpdateWarrantyRequest();
        updateRequest.setStatus("IN_REVIEW");

        when(warrantyRepository.findById(1L)).thenReturn(Optional.of(warrantyPrueba));

        assertThatThrownBy(() -> warrantyService.updateWarranty(1L, updateRequest))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("No se puede actualizar una garantía ya cerrada");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — no se puede modificar garantía cerrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar una garantía inexistente")
    public void shouldNotUpdateWarrantyWhenNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción al actualizar una garantía inexistente");
        System.out.println("  → Intentando actualizar garantía con ID=9999 (no existe)");

        UpdateWarrantyRequest updateRequest = new UpdateWarrantyRequest();
        updateRequest.setStatus("IN_REVIEW");

        when(warrantyRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warrantyService.updateWarranty(9999L, updateRequest))
                .isInstanceOf(WarrantyNotFoundException.class)
                .hasMessageContaining("Garantía no encontrada con ID: 9999");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyNotFoundException lanzada correctamente");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── closeWarranty ────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe cerrar una garantía con resolución válida")
    public void shouldCloseWarranty() {
        System.out.println("\n[TEST] Debe cerrar una garantía con resolución válida");
        System.out.println("  → Cerrando garantía ID=1 con resolución='Se reemplazó el producto'");

        CloseWarrantyRequest closeRequest = new CloseWarrantyRequest();
        closeRequest.setResolution("Se reemplazó el producto");

        when(warrantyRepository.findById(1L)).thenReturn(Optional.of(warrantyPrueba));
        when(warrantyRepository.save(any(Warranty.class))).thenAnswer(inv -> inv.getArgument(0));

        WarrantyResponse result = warrantyService.closeWarranty(1L, closeRequest);

        System.out.println("  → Resultado: estado=" + result.getStatus() + " | resolución='" + result.getResolution() + "'");
        assertThat(result.getStatus()).isEqualTo("CLOSED");
        assertThat(result.getResolution()).isEqualTo("Se reemplazó el producto");
        verify(warrantyRepository, times(1)).save(warrantyPrueba);
        System.out.println("  ✓ Garantía cerrada correctamente con estado CLOSED");
    }

    @Test
    @DisplayName("Debe lanzar excepción al cerrar una garantía ya cerrada")
    public void shouldNotCloseAlreadyClosedWarranty() {
        System.out.println("\n[TEST] Debe lanzar excepción al cerrar una garantía ya cerrada");
        warrantyPrueba.setStatus("CLOSED");
        System.out.println("  → Intentando cerrar garantía ID=1 que ya tiene estado=CLOSED");

        CloseWarrantyRequest closeRequest = new CloseWarrantyRequest();
        closeRequest.setResolution("Resolución cualquiera");

        when(warrantyRepository.findById(1L)).thenReturn(Optional.of(warrantyPrueba));

        assertThatThrownBy(() -> warrantyService.closeWarranty(1L, closeRequest))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("La garantía ya está cerrada");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — garantía ya cerrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción al cerrar una garantía sin resolución")
    public void shouldNotCloseWarrantyWithoutResolution() {
        System.out.println("\n[TEST] Debe lanzar excepción al cerrar una garantía sin resolución");
        System.out.println("  → Intentando cerrar garantía ID=1 con resolución en blanco '  '");

        CloseWarrantyRequest closeRequest = new CloseWarrantyRequest();
        closeRequest.setResolution("  ");

        when(warrantyRepository.findById(1L)).thenReturn(Optional.of(warrantyPrueba));

        assertThatThrownBy(() -> warrantyService.closeWarranty(1L, closeRequest))
                .isInstanceOf(WarrantyInvalidException.class)
                .hasMessageContaining("No se puede cerrar una garantía sin resolución");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyInvalidException lanzada correctamente — resolución vacía");
    }

    @Test
    @DisplayName("Debe lanzar excepción al cerrar una garantía inexistente")
    public void shouldNotCloseWarrantyWhenNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción al cerrar una garantía inexistente");
        System.out.println("  → Intentando cerrar garantía con ID=9999 (no existe)");

        CloseWarrantyRequest closeRequest = new CloseWarrantyRequest();
        closeRequest.setResolution("Resolución válida");

        when(warrantyRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warrantyService.closeWarranty(9999L, closeRequest))
                .isInstanceOf(WarrantyNotFoundException.class)
                .hasMessageContaining("Garantía no encontrada con ID: 9999");

        verify(warrantyRepository, never()).save(any(Warranty.class));
        System.out.println("  ✓ Excepción WarrantyNotFoundException lanzada correctamente");
    }
}