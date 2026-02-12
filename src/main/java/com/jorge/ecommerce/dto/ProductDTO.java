package com.jorge.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class ProductDTO implements Serializable{
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
