package com.GameHubStore.product_service.controller;

import com.GameHubStore.product_service.exception.BusinessException;
import com.GameHubStore.product_service.exception.ProductNotFoundException;
import com.GameHubStore.product_service.model.dto.ProductRequest;
import com.GameHubStore.product_service.model.dto.ProductResponse;
import com.GameHubStore.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductResponse productResponse;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        productResponse = ProductResponse.builder()
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

    @Test
    @DisplayName("GET /api/product debe retornar 200 y la lista de productos")
    void getAllProducts_deberiaRetornar200YListado() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("PlayStation 5"))
                .andExpect(jsonPath("$[0].marca").value("Sony"));
    }

    @Test
    @DisplayName("GET /api/product/{id} debe retornar 200 cuando el producto existe")
    void getProductById_deberiaRetornar200() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/api/product/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /api/product/{id} debe retornar 404 cuando el producto no existe")
    void getProductById_deberiaRetornar404SiNoExiste() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ProductNotFoundException("Producto no encontrado con ID: 99"));

        mockMvc.perform(get("/api/product/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Producto no encontrado con ID: 99"));
    }

    @Test
    @DisplayName("GET /api/product/marca/{marca} debe retornar 200 con la lista filtrada")
    void getProductByMarca_deberiaRetornar200() throws Exception {
        when(productService.getProductByMarca("sony")).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/product/marca/{marca}", "sony"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Sony"));
    }

    @Test
    @DisplayName("POST /api/product debe retornar 201 con un request valido")
    void addProduct_deberiaRetornar201() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .nombre("PlayStation 5")
                .marca("Sony")
                .modelo("CFI-1215A")
                .precio(549.99)
                .categoriaId("consolas")
                .descripcion("Consola PlayStation 5 con lector de disco")
                .estado(true)
                .build();

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(productService).addProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("POST /api/product debe retornar 400 si faltan campos obligatorios")
    void addProduct_deberiaRetornar400SiFaltanCampos() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .nombre("") // invalido: @NotBlank
                .build();

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("PUT /api/product/{id}/precio debe retornar 200 con precio actualizado")
    void updateProduct_deberiaRetornar200() throws Exception {
        ProductResponse actualizado = ProductResponse.builder()
                .id(1L).nombre("PlayStation 5").marca("Sony").precio(499.99).estado(true).build();

        when(productService.updatePrecio(eq(1L), eq(499.99))).thenReturn(actualizado);

        mockMvc.perform(put("/api/product/{id}/precio", 1L)
                        .param("precio", "499.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(499.99));
    }

    @Test
    @DisplayName("PUT /api/product/{id}/precio debe retornar 400 si el precio es invalido")
    void updateProduct_deberiaRetornar400SiPrecioInvalido() throws Exception {
        when(productService.updatePrecio(eq(1L), eq(-10.0)))
                .thenThrow(new BusinessException("El precio debe ser mayor a cero"));

        mockMvc.perform(put("/api/product/{id}/precio", 1L)
                        .param("precio", "-10.0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/product/{id}/desactivar debe retornar 200")
    void desactivarProducto_deberiaRetornar200() throws Exception {
        ProductResponse desactivado = ProductResponse.builder()
                .id(1L).nombre("PlayStation 5").estado(false).build();

        when(productService.desactivarProducto(1L)).thenReturn(desactivado);

        mockMvc.perform(patch("/api/product/{id}/desactivar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    @DisplayName("PATCH /api/product/{id}/desactivar debe retornar 404 si no existe")
    void desactivarProducto_deberiaRetornar404SiNoExiste() throws Exception {
        when(productService.desactivarProducto(99L))
                .thenThrow(new ProductNotFoundException("Producto no encontrado con ID: 99"));

        mockMvc.perform(patch("/api/product/{id}/desactivar", 99L))
                .andExpect(status().isNotFound());
    }
}
