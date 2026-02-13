package com.jorge.ecommerce.service;

import com.jorge.ecommerce.dto.CartDTO;
import com.jorge.ecommerce.dto.WishlistDTO;
import com.jorge.ecommerce.entity.Product;
import com.jorge.ecommerce.entity.User;
import com.jorge.ecommerce.entity.Wishlist;
import com.jorge.ecommerce.exception.BadRequestException;
import com.jorge.ecommerce.exception.ResourceNotFoundException;
import com.jorge.ecommerce.repository.ProductRepository;
import com.jorge.ecommerce.repository.UserRepository;
import com.jorge.ecommerce.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<WishlistDTO> getWishlist(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return wishlistRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistDTO addToWishlist(String userEmail, Long productId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new BadRequestException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        return convertToDTO(wishlistRepository.save(wishlist));
    }

    @Transactional
    public void removeFromWishlist(String userEmail, Long productId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new ResourceNotFoundException("Product not found in wishlist");
        }

        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    @Transactional
    public CartDTO moveToCart(String userEmail, Long productId, CartService cartService) {
        removeFromWishlist(userEmail, productId);
        com.jorge.ecommerce.dto.AddToCartDTO addToCartDTO = new com.jorge.ecommerce.dto.AddToCartDTO();
        addToCartDTO.setProductId(productId);
        addToCartDTO.setQuantity(1);
        return cartService.addToCart(userEmail, addToCartDTO);
    }

    private WishlistDTO convertToDTO(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setProductId(wishlist.getProduct().getId());
        dto.setProductName(wishlist.getProduct().getName());
        dto.setProductImage(wishlist.getProduct().getImageUrl());
        dto.setProductPrice(wishlist.getProduct().getPrice());
        dto.setProductActive(wishlist.getProduct().getActive());
        dto.setCreatedAt(wishlist.getCreatedAt());
        return dto;
    }
}