package com.jorge.ecommerce.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

@Data
public class CategoryDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer productCount;
    private LocalDateTime createdAt;
}