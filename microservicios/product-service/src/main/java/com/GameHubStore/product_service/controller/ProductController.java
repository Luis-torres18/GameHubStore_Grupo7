package com.GameHubStore.product_service.controller;

import com.GameHubStore.product_service.model.dto.ProductRequest;
import com.GameHubStore.product_service.model.dto.ProductResponse;
import com.GameHubStore.product_service.model.entities.Product;
import com.GameHubStore.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "Productos v1", description = "Metodos CRUD para gestionar productos")
public class ProductController {

    public final ProductService productService;

    // Listar todos los productos
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Listar Productos",
            description = "Retorna una lista de todos los productos disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de productos exitoso")
    public List<ProductResponse> getAllProducts(){
        return this.productService.getAllProducts();
    }

    // Listar un producto por ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener Producto por ID",
            description = "Retorna un producto por su ID")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = {
                                @ExampleObject(
                                    name = "Ejemplo Producto",
                                    value = "{\"nombre\": \"PlayStation 5\", " +
                                            "\"marca\": \"Sony\", " +
                                            "\"modelo\": \"CFI-1215A\", " +
                                            "\"precio\": 549.99, " +
                                            "\"categoriaId\": \"consolas\", " +
                                            "\"descripcion\": \"Consola PlayStation 5 con lector de disco\", " +
                                            "\"estado\": true}"
                                )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado")
    })
    public List<ProductResponse> getProductById(
            @Parameter(description = "ID del producto a buscar", required = true, example = "1")
            @PathVariable Long id
    ){
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Información del producto a agregar", required = true,
            content = @Content(schema = @Schema(implementation = ProductRequest.class))
    )
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
