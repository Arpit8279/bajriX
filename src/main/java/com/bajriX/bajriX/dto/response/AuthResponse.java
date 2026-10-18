package com.bajriX.bajriX.dto.response;

public record AuthResponse(
    Long id,
    String email,
    String fullName,
    String role,
    Long sellerId,
    String businessName,
    String token
) {}
