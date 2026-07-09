package com.GameHubStore.product_service.repository;

import com.GameHubStore.product_service.model.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("findByMarcaIgnoreCase debe encontrar productos sin importar mayusculas/minusculas")
    void findByMarcaIgnoreCase_deberiaEncontrarProductosIgnorandoCase() {
        productRepository.save(Product.builder()
                .nombre("PlayStation 5")
                .marca("Sony")
                .modelo("CFI-1215A")
                .precio(549.99)
                .categoriaId("consolas")
                .estado(true)
                .build());

        productRepository.save(Product.builder()
                .nombre("Xbox Series X")
                .marca("Microsoft")
                .modelo("1882")
                .precio(499.99)
                .categoriaId("consolas")
                .estado(true)
                .build());

        List<Product> resultado = productRepository.findByMarcaIgnoreCase("sony");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("PlayStation 5");
    }

    @Test
    @DisplayName("findByMarcaIgnoreCase debe retornar lista vacia si no hay coincidencias")
    void findByMarcaIgnoreCase_deberiaRetornarListaVacia() {
        List<Product> resultado = productRepository.findByMarcaIgnoreCase("nintendo");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("save debe persistir el producto y generar un ID")
    void save_deberiaPersistirYGenerarId() {
        Product producto = Product.builder()
                .nombre("Nintendo Switch 2")
                .marca("Nintendo")
                .modelo("HEG-001")
                .precio(449.99)
                .categoriaId("consolas")
                .estado(true)
                .build();

        Product guardado = productRepository.save(producto);

        assertThat(guardado.getId()).isNotNull();
    }
}
