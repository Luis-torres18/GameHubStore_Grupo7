package com.GameHubStore.product_service.service;

import com.GameHubStore.product_service.exception.BusinessException;
import com.GameHubStore.product_service.exception.ProductNotFoundException;
import com.GameHubStore.product_service.model.dto.ProductRequest;
import com.GameHubStore.product_service.model.dto.ProductResponse;
import com.GameHubStore.product_service.model.entities.Product;
import com.GameHubStore.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    private Product productoGuardado;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);

        productoGuardado = Product.builder()
                .id(1L)
                .nombre("PlayStation 5")
                .marca("Sony")
                .modelo("CFI-1215A")
                .precio(549.99)
                .categoriaId("consolas")
                .descripcion("Consola PlayStation 5 con lector de disco")
                .estado(true)
                .build();
    }

    // ---------- addProduct ----------

    @Test
    @DisplayName("addProduct debe mapear el request y guardarlo en el repositorio")
    void addProduct_deberiaGuardarProducto() {
        ProductRequest request = ProductRequest.builder()
                .nombre("PlayStation 5")
                .marca("Sony")
                .modelo("CFI-1215A")
                .precio(549.99)
                .categoriaId("consolas")
                .descripcion("Consola PlayStation 5 con lector de disco")
                .estado(true)
                .build();

        productService.addProduct(request);

        verify(productRepository).save(productCaptor.capture());
        Product productoCapturado = productCaptor.getValue();

        assertThat(productoCapturado.getNombre()).isEqualTo("PlayStation 5");
        assertThat(productoCapturado.getMarca()).isEqualTo("Sony");
        assertThat(productoCapturado.getPrecio()).isEqualTo(549.99);
        assertThat(productoCapturado.getEstado()).isTrue();
    }

    // ---------- getAllProducts ----------

    @Test
    @DisplayName("getAllProducts debe retornar la lista mapeada a ProductResponse")
    void getAllProducts_deberiaRetornarListaMapeada() {
        when(productRepository.findAll()).thenReturn(List.of(productoGuardado));

        List<ProductResponse> resultado = productService.getAllProducts();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("PlayStation 5");
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getAllProducts debe retornar lista vacia si no hay productos")
    void getAllProducts_deberiaRetornarListaVacia() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> resultado = productService.getAllProducts();

        assertThat(resultado).isEmpty();
    }

    // ---------- getProductById ----------

    @Test
    @DisplayName("getProductById debe retornar el producto cuando existe")
    void getProductById_deberiaRetornarProducto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productoGuardado));

        ProductResponse resultado = productService.getProductById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("PlayStation 5");
    }

    @Test
    @DisplayName("getProductById debe lanzar ProductNotFoundException si no existe")
    void getProductById_deberiaLanzarExcepcionSiNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---------- getProductByMarca ----------

    @Test
    @DisplayName("getProductByMarca debe retornar productos filtrados por marca")
    void getProductByMarca_deberiaRetornarProductosDeLaMarca() {
        when(productRepository.findByMarcaIgnoreCase("sony")).thenReturn(List.of(productoGuardado));

        List<ProductResponse> resultado = productService.getProductByMarca("sony");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMarca()).isEqualTo("Sony");
    }

    @Test
    @DisplayName("getProductByMarca debe retornar lista vacia si la marca no tiene productos")
    void getProductByMarca_deberiaRetornarListaVaciaSiNoHayCoincidencias() {
        when(productRepository.findByMarcaIgnoreCase("nintendo")).thenReturn(List.of());

        List<ProductResponse> resultado = productService.getProductByMarca("nintendo");

        assertThat(resultado).isEmpty();
    }

    // ---------- updatePrecio ----------

    @Test
    @DisplayName("updatePrecio debe actualizar el precio cuando es valido")
    void updatePrecio_deberiaActualizarPrecio() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productoGuardado));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse resultado = productService.updatePrecio(1L, 499.99);

        assertThat(resultado.getPrecio()).isEqualTo(499.99);
        verify(productRepository).save(productoGuardado);
    }

    @Test
    @DisplayName("updatePrecio debe lanzar BusinessException si el precio es null")
    void updatePrecio_deberiaLanzarExcepcionSiPrecioEsNull() {
        assertThatThrownBy(() -> productService.updatePrecio(1L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mayor a cero");

        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("updatePrecio debe lanzar BusinessException si el precio es <= 0")
    void updatePrecio_deberiaLanzarExcepcionSiPrecioEsNegativoOCero() {
        assertThatThrownBy(() -> productService.updatePrecio(1L, 0.0))
                .isInstanceOf(BusinessException.class);

        assertThatThrownBy(() -> productService.updatePrecio(1L, -10.0))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("updatePrecio debe lanzar ProductNotFoundException si el producto no existe")
    void updatePrecio_deberiaLanzarExcepcionSiProductoNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updatePrecio(99L, 100.0))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    // ---------- desactivarProducto ----------

    @Test
    @DisplayName("desactivarProducto debe cambiar el estado a false")
    void desactivarProducto_deberiaDesactivarProducto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productoGuardado));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse resultado = productService.desactivarProducto(1L);

        assertThat(resultado.getEstado()).isFalse();
    }

    @Test
    @DisplayName("desactivarProducto debe lanzar BusinessException si ya esta desactivado")
    void desactivarProducto_deberiaLanzarExcepcionSiYaEstaDesactivado() {
        productoGuardado.setEstado(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(productoGuardado));

        assertThatThrownBy(() -> productService.desactivarProducto(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya se encuentra desactivado");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("desactivarProducto debe lanzar ProductNotFoundException si no existe")
    void desactivarProducto_deberiaLanzarExcepcionSiProductoNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.desactivarProducto(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
