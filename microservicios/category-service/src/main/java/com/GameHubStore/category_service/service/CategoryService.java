package com.GameHubStore.category_service.service;

import com.GameHubStore.category_service.exception.BusinessException;
import com.GameHubStore.category_service.exception.CategoryNotFoundException;
import com.GameHubStore.category_service.model.dto.CategoryRequest;
import com.GameHubStore.category_service.model.dto.CategoryResponse;
import com.GameHubStore.category_service.model.entities.Category;
import com.GameHubStore.category_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse addCategory(CategoryRequest request){
        if (categoryRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new BusinessException(
                    "Ya existe una categoria con el nombre: '" +request.getNombre()+"'");
        }
        var category = Category.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .estado(request.getEstado())
                .build();

        categoryRepository.save(category);
        log.info("Categoria agregada exitosamente como: {}", category.getNombre());
        return mapToCategoryResponse(category);
    }

    public List<CategoryResponse> getAllCategories(){
        var categories = categoryRepository.findAll();

        return categories.stream().map(this::mapToCategoryResponse).toList();
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .nombre(category.getNombre())
                .descripcion(category.getDescripcion())
                .estado(category.getEstado())
                .build();
    }

    public CategoryResponse getCategoryById(Long id){
        Category category = findCategoryById(id);
        return mapToCategoryResponse(category);
    }

    private Category findCategoryById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Categoria no encontrada con ID: " + id));
    }

    public CategoryResponse updateCategory(Long id, String nombre){
        if (categoryRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)){
            throw new BusinessException(
                    "Ya existe una categoria con el nombre: " + nombre);
        }

        Category category = findCategoryById(id);
        category.setNombre(nombre);
        category = categoryRepository.save(category);
        log.info("Categoria '{}' (id={}) actualizada", category.getId(), category.getNombre());
        return mapToCategoryResponse(category);
    }

    private Category findCategoryOrThrow(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Categoria no encontrada con ID: " + id));
    }

    public CategoryResponse desactivarCategoria(Long id){
        Category category = findCategoryOrThrow(id);

        if (Boolean.FALSE.equals(category.getEstado())){
            throw new BusinessException("La categoria ya se encuentra desactivada");
        }

        category.setEstado(false);
        category = categoryRepository.save(category);
        log.info("Categoria '{}' (id={}) desactivada", category.getNombre(), category.getId());
        return mapToCategoryResponse(category);
    }
}
