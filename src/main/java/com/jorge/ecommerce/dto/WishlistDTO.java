package com.jorge.ecommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishlistDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private java.math.BigDecimal productPrice;
    private Boolean productActive;
    private LocalDateTime createdAt;
}