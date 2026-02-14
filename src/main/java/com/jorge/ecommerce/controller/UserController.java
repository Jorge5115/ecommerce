package com.jorge.ecommerce.controller;

import com.jorge.ecommerce.dto.UpdateProfileDTO;
import com.jorge.ecommerce.dto.UserDTO;
import com.jorge.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints de usuarios")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Ver mi perfil")
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @Operation(summary = "Actualizar mi perfil")
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileDTO updateDTO
    ) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), updateDTO));
    }
}
