package com.jorge.ecommerce.controller;

import com.jorge.ecommerce.dto.AddToCartDTO;
import com.jorge.ecommerce.dto.CartDTO;
import com.jorge.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Endpoints del carrito")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Ver carrito actual")
    @GetMapping
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @Operation(summary = "Añadir producto al carrito")
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartDTO addToCartDTO
    ) {
        return ResponseEntity.ok(cartService.addToCart(userDetails.getUsername(), addToCartDTO));
    }

    @Operation(summary = "Actualizar cantidad de producto")
    @PutMapping("/update/{productId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(userDetails.getUsername(), productId, quantity));
    }

    @Operation(summary = "Eliminar producto del carrito")
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(cartService.removeFromCart(userDetails.getUsername(), productId));
    }

    @Operation(summary = "Vaciar carrito")
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}