package com.jorge.ecommerce.service;

import com.jorge.ecommerce.dto.CreateProductDTO;
import com.jorge.ecommerce.dto.ProductDTO;
import com.jorge.ecommerce.dto.UpdateProductDTO;
import com.jorge.ecommerce.entity.Category;
import com.jorge.ecommerce.entity.Product;
import com.jorge.ecommerce.exception.ResourceNotFoundException;
import com.jorge.ecommerce.repository.CategoryRepository;
import com.jorge.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDTO);
    }


    @Cacheable(value = "product", key = "#id")
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    @CacheEvict(value = {"product", "products"}, allEntries = true)
    @Transactional
    public ProductDTO createProduct(CreateProductDTO createDTO) {
        Category category = categoryRepository.findById(createDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + createDTO.getCategoryId()));

        Product product = new Product();
        product.setName(createDTO.getName());
        product.setDescription(createDTO.getDescription());
        product.setPrice(createDTO.getPrice());
        product.setStock(createDTO.getStock());
        product.setImageUrl(createDTO.getImageUrl());
        product.setCategory(category);
        product.setActive(true);

        return convertToDTO(productRepository.save(product));
    }

    @CacheEvict(value = {"product", "products"}, allEntries = true)
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductDTO updateDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (updateDTO.getName() != null) product.setName(updateDTO.getName());
        if (updateDTO.getDescription() != null) product.setDescription(updateDTO.getDescription());
        if (updateDTO.getPrice() != null) product.setPrice(updateDTO.getPrice());
        if (updateDTO.getStock() != null) product.setStock(updateDTO.getStock());
        if (updateDTO.getImageUrl() != null) product.setImageUrl(updateDTO.getImageUrl());
        if (updateDTO.getActive() != null) product.setActive(updateDTO.getActive());
        if (updateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + updateDTO.getCategoryId()));
            product.setCategory(category);
        }

        return convertToDTO(productRepository.save(product));
    }

    @CacheEvict(value = {"product", "products"}, allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setActive(product.getActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(
            String search,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {
        return productRepository.searchProducts(search, categoryId, minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }
}