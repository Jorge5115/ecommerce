package com.jorge.ecommerce.service;

import com.jorge.ecommerce.dto.CouponDTO;
import com.jorge.ecommerce.dto.CreateCouponDTO;
import com.jorge.ecommerce.dto.ValidateCouponDTO;
import com.jorge.ecommerce.entity.Coupon;
import com.jorge.ecommerce.exception.BadRequestException;
import com.jorge.ecommerce.exception.ResourceNotFoundException;
import com.jorge.ecommerce.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponDTO createCoupon(CreateCouponDTO createDTO) {
        if (couponRepository.existsByCode(createDTO.getCode())) {
            throw new BadRequestException("Coupon code already exists: " + createDTO.getCode());
        }

        if (createDTO.getDiscountPercentage() == null && createDTO.getDiscountAmount() == null) {
            throw new BadRequestException("Either discount percentage or discount amount is required");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(createDTO.getCode().toUpperCase());
        coupon.setDiscountPercentage(createDTO.getDiscountPercentage());
        coupon.setDiscountAmount(createDTO.getDiscountAmount());
        coupon.setMinPurchaseAmount(createDTO.getMinPurchaseAmount());
        coupon.setValidFrom(createDTO.getValidFrom());
        coupon.setValidTo(createDTO.getValidTo());
        coupon.setUsageLimit(createDTO.getUsageLimit());
        coupon.setUsedCount(0);
        coupon.setActive(true);

        return convertToDTO(couponRepository.save(coupon));
    }

    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CouponDTO> getActiveCoupons() {
        return couponRepository.findByActive(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ValidateCouponDTO validateCoupon(String code, BigDecimal cartTotal) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElse(null);

        if (coupon == null) {
            return new ValidateCouponDTO(false, "Coupon not found", BigDecimal.ZERO, cartTotal);
        }

        if (!coupon.getActive()) {
            return new ValidateCouponDTO(false, "Coupon is not active", BigDecimal.ZERO, cartTotal);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTo())) {
            return new ValidateCouponDTO(false, "Coupon has expired or is not yet valid", BigDecimal.ZERO, cartTotal);
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return new ValidateCouponDTO(false, "Coupon usage limit reached", BigDecimal.ZERO, cartTotal);
        }

        if (coupon.getMinPurchaseAmount() != null && cartTotal.compareTo(coupon.getMinPurchaseAmount()) < 0) {
            return new ValidateCouponDTO(false,
                    "Minimum purchase amount is " + coupon.getMinPurchaseAmount(),
                    BigDecimal.ZERO, cartTotal);
        }

        BigDecimal discount = calculateDiscount(coupon, cartTotal);
        BigDecimal finalTotal = cartTotal.subtract(discount);

        return new ValidateCouponDTO(true, "Coupon is valid", discount, finalTotal);
    }

    @Transactional
    public CouponDTO toggleCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setActive(!coupon.getActive());
        return convertToDTO(couponRepository.save(coupon));
    }

    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found");
        }
        couponRepository.deleteById(id);
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal cartTotal) {
        if (coupon.getDiscountPercentage() != null) {
            return cartTotal.multiply(coupon.getDiscountPercentage().divide(new BigDecimal("100")));
        }
        if (coupon.getDiscountAmount() != null) {
            return coupon.getDiscountAmount().min(cartTotal);
        }
        return BigDecimal.ZERO;
    }

    private CouponDTO convertToDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDiscountPercentage(coupon.getDiscountPercentage());
        dto.setDiscountAmount(coupon.getDiscountAmount());
        dto.setMinPurchaseAmount(coupon.getMinPurchaseAmount());
        dto.setValidFrom(coupon.getValidFrom());
        dto.setValidTo(coupon.getValidTo());
        dto.setUsageLimit(coupon.getUsageLimit());
        dto.setUsedCount(coupon.getUsedCount());
        dto.setActive(coupon.getActive());
        dto.setCreatedAt(coupon.getCreatedAt());
        return dto;
    }
}