package com.bajriX.bajriX.dto.response;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    String role,
    Long sellerId,
    String businessName
) {}
