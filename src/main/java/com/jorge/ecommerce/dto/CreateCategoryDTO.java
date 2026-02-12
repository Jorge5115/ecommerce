package com.jorge.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryDTO {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String imageUrl;
}