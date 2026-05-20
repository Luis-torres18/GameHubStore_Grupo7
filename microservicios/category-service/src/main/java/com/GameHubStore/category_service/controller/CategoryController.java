package com.GameHubStore.category_service.controller;

import com.GameHubStore.category_service.model.dto.CategoryRequest;
import com.GameHubStore.category_service.model.dto.CategoryResponse;
import com.GameHubStore.category_service.model.dto.CategoryUpdateRequest;
import com.GameHubStore.category_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Agregar una categoria
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addCategory(@RequestBody CategoryRequest categoryRequest){
        this.categoryService.addCategory(categoryRequest);
    }

    // Listar todas las categorias
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponse> getAllCategories(){
        return this.categoryService.getAllCategories();
    }

    // Listar categoria por ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getCategoryById(@PathVariable Long id){
        return this.categoryService.getCategoryById(id);
    }

    // Actualizar nombre a una categoria
    @PutMapping("/{id}/nombre")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request){
        return this.categoryService.updateCategory(id, request.getNombre());
    }

    // Desactivar categoria
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse desactivarCategoria(@PathVariable Long id){
        return this.categoryService.desactivarCategoria(id);
    }
}
