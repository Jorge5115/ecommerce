package com.jorge.ecommerce.service;

import com.jorge.ecommerce.dto.CategoryDTO;
import com.jorge.ecommerce.dto.CreateCategoryDTO;
import com.jorge.ecommerce.entity.Category;
import com.jorge.ecommerce.exception.BadRequestException;
import com.jorge.ecommerce.exception.ResourceNotFoundException;
import com.jorge.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories")
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Cacheable(value = "category", key = "#id")
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDTO(category);
    }

    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    @Transactional
    public CategoryDTO createCategory(CreateCategoryDTO createDTO) {
        if (categoryRepository.existsByName(createDTO.getName())) {
            throw new BadRequestException("Category already exists with name: " + createDTO.getName());
        }

        Category category = new Category();
        category.setName(createDTO.getName());
        category.setDescription(createDTO.getDescription());
        category.setImageUrl(createDTO.getImageUrl());

        return convertToDTO(categoryRepository.save(category));
    }

    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    @Transactional
    public CategoryDTO updateCategory(Long id, CreateCategoryDTO updateDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (updateDTO.getName() != null) category.setName(updateDTO.getName());
        if (updateDTO.getDescription() != null) category.setDescription(updateDTO.getDescription());
        if (updateDTO.getImageUrl() != null) category.setImageUrl(updateDTO.getImageUrl());

        return convertToDTO(categoryRepository.save(category));
    }

    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setProductCount(category.getProducts() != null ? category.getProducts().size() : 0);
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }
}