package com.GameHubStore.product_service.controller;

import com.GameHubStore.product_service.model.dto.ProductRequest;
import com.GameHubStore.product_service.model.dto.ProductResponse;
import com.GameHubStore.product_service.model.entities.Product;
import com.GameHubStore.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    public final ProductService productService;

    // Listar todos los productos
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){
        return this.productService.getAllProducts();
    }

    // Listar un producto por ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductById(@PathVariable Long id){
        return Collections.singletonList(this.productService.getProductById(id));
    }

    // Listar productos por marca
    @GetMapping("/marca/{marca}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductByMarca(@PathVariable String marca){
        return this.productService.getProductByMarca(marca);
    }

    // Agregar un producto
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody ProductRequest productRequest){
        this.productService.addProduct(productRequest);
    }

    // Actualizar precio de un producto
    @PutMapping("/{id}/precio")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @RequestParam Double precio){
        return productService.updatePrecio(id, precio);
    }

    // Desactivar un producto
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse desactivarProducto(@PathVariable Long id){
        return productService.desactivarProducto(id);
    }


}
