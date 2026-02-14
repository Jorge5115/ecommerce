package com.jorge.ecommerce.controller;

import com.jorge.ecommerce.dto.CreateReviewDTO;
import com.jorge.ecommerce.dto.ReviewDTO;
import com.jorge.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Endpoints de reseñas")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Crear reseña de producto")
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewDTO createReviewDTO
    ) {
        return new ResponseEntity<>(
                reviewService.createReview(userDetails.getUsername(), createReviewDTO),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Listar reseñas de un producto")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @Operation(summary = "Eliminar reseña")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        reviewService.deleteReview(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}