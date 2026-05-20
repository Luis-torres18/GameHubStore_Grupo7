package com.GameHubStore.product_service.repository;

import com.GameHubStore.product_service.model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByMarcaIgnoreCase(String marca);
}
