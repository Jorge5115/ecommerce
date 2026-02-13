package com.jorge.ecommerce.controller;

import com.jorge.ecommerce.dto.CouponDTO;
import com.jorge.ecommerce.dto.CreateCouponDTO;
import com.jorge.ecommerce.dto.ValidateCouponDTO;
import com.jorge.ecommerce.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponDTO> createCoupon(@Valid @RequestBody CreateCouponDTO createDTO) {
        return new ResponseEntity<>(couponService.createCoupon(createDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CouponDTO>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/active")
    public ResponseEntity<List<CouponDTO>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateCouponDTO> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal cartTotal
    ) {
        return ResponseEntity.ok(couponService.validateCoupon(code, cartTotal));
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponDTO> toggleCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.toggleCoupon(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}