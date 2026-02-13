package com.jorge.ecommerce.controller;

import com.jorge.ecommerce.dto.CartDTO;
import com.jorge.ecommerce.dto.WishlistDTO;
import com.jorge.ecommerce.service.CartService;
import com.jorge.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<WishlistDTO>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<WishlistDTO> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId
    ) {
        return new ResponseEntity<>(
                wishlistService.addToWishlist(userDetails.getUsername(), productId),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId
    ) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/move-to-cart")
    public ResponseEntity<CartDTO> moveToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                wishlistService.moveToCart(userDetails.getUsername(), productId, cartService)
        );
    }
}