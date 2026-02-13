package com.jorge.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ValidateCouponDTO {
    private Boolean valid;
    private String message;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
}