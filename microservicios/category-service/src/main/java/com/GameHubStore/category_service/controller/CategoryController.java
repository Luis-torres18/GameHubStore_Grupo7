package com.GameHubStore.category_service.controller;

import com.GameHubStore.category_service.model.dto.CategoryRequest;
import com.GameHubStore.category_service.model.dto.CategoryResponse;
import com.GameHubStore.category_service.model.dto.CategoryUpdateRequest;
import com.GameHubStore.category_service.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Agregar una categoria",
            description = "Permite agregar una nueva categoria"
    )
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categoria agregada exitosamente"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error al agregar la categoria")
            }
    )
    public void addCategory(@RequestBody CategoryRequest categoryRequest){
        this.categoryService.addCategory(categoryRequest);
    }

    // Listar todas las categorias
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Listar todas las categorias",
            description = "Permite listar todas las categorias"
    )
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categorias listadas exitosamente"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error al listar las categorias")
            }
    )
    public List<CategoryResponse> getAllCategories(){
        return this.categoryService.getAllCategories();
    }

    // Listar categoria por ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Listar categoria por ID",
            description = "Permite listar una categoria por su ID"
    )
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categoria encontrada"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Categoria no encontrada")
            }
    )
    public CategoryResponse getCategoryById(@PathVariable Long id){
        return this.categoryService.getCategoryById(id);
    }

    // Actualizar nombre a una categoria
    @PutMapping("/{id}/nombre")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Actualizar nombre a una categoria",
            description = "Permite actualizar el nombre de una categoria"
    )
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categoria actualizada"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Categoria no encontrada")
            }
    )
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request){
        return this.categoryService.updateCategory(id, request.getNombre());
    }

    // Desactivar categoria
    @PatchMapping("/{id}/desactivar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Desactiva una categoria",
            description = "Permite desactivar una categoria"
    )
    @ApiResponses(
            value ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categoria desactivada"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Categoria no encontrada")
            }
    )
    public CategoryResponse desactivarCategoria(@PathVariable Long id){
        return this.categoryService.desactivarCategoria(id);
    }
}
