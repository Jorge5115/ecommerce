package com.jorge.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCouponDTO {

    @NotBlank(message = "Coupon code is required")
    private String code;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal minPurchaseAmount;

    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;

    @NotNull(message = "Valid to date is required")
    private LocalDateTime validTo;

    private Integer usageLimit;
}