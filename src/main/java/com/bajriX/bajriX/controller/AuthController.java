package com.bajriX.bajriX.controller;

import com.bajriX.bajriX.dto.request.LoginRequest;
import com.bajriX.bajriX.dto.response.ApiResponse;
import com.bajriX.bajriX.dto.response.AuthResponse;
import com.bajriX.bajriX.dto.response.UserResponse;
import com.bajriX.bajriX.exception.AuthenticationException;
import com.bajriX.bajriX.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            throw new AuthenticationException("Authorization header is required");
        }
        UserResponse response = authService.getCurrentUser(authHeader);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
