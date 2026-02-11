package com.jorge.ecommerce.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateProductDTO {

    private String name;
    private String description;

    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock must be positive")
    private Integer stock;

    private String imageUrl;
    private Long categoryId;
    private Boolean active;
}