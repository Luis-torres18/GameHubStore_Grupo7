package com.GameHubStore.product_service.service;

import com.GameHubStore.product_service.exception.BusinessException;
import com.GameHubStore.product_service.exception.ProductNotFoundException;
import com.GameHubStore.product_service.model.dto.ProductRequest;
import com.GameHubStore.product_service.model.dto.ProductResponse;
import com.GameHubStore.product_service.model.entities.Product;
import com.GameHubStore.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void addProduct(ProductRequest productRequest){
        var product = Product.builder()
                .nombre(productRequest.getNombre())
                .marca(productRequest.getMarca())
                .modelo(productRequest.getModelo())
                .precio(productRequest.getPrecio())
                .categoriaId(productRequest.getCategoriaId())
                .descripcion(productRequest.getDescripcion())
                .estado(productRequest.getEstado())
                .build();

        productRepository.save(product);
        log.info("Producto agregado exitosamente como: {}", product.getNombre());
    }

    public List<ProductResponse> getAllProducts(){
        var products = productRepository.findAll();
        
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .nombre(product.getNombre())
                .marca(product.getMarca())
                .modelo(product.getModelo())
                .precio(product.getPrecio())
                .categoriaId(product.getCategoriaId())
                .descripcion(product.getDescripcion())
                .estado(product.getEstado())
                .build();
    }

    public ProductResponse getProductById(Long id){
        Product product = findProductById(id);
        return mapToProductResponse(product);
    }

    private Product findProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con ID: " + id));
    }

    public List<ProductResponse> getProductByMarca(String marca){
        return productRepository.findByMarcaIgnoreCase(marca)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    public ProductResponse updatePrecio(Long id, Double precio){
        if (precio == null || precio <= 0){
            throw new BusinessException("El precio debe ser mayor a cero");
        }
        Product product = findProductOrThrow(id);
        product.setPrecio(precio);
        product = productRepository.save(product);
        log.info("Precio actuaizado del producto '{}' (id={}): {}", product.getNombre(), product.getId(), product.getPrecio());
        return mapToProductResponse(product);
    }

    private Product findProductOrThrow(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto no encontrado con ID: " + id));
    }

    public ProductResponse desactivarProducto(Long id){
        Product product = findProductOrThrow(id);

        if (Boolean.FALSE.equals(product.getEstado())){
            throw new BusinessException("El producto con id:"+ id + " ya se encuentra desactivado");
        }

        product.setEstado(Boolean.FALSE);
        product = productRepository.save(product);
        log.info("Producto '{}' (id={}) desactivado", product.getNombre(), product.getId());
        return mapToProductResponse(product);
    }
}
