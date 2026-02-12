package com.jorge.ecommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer productCount;
    private LocalDateTime createdAt;
}