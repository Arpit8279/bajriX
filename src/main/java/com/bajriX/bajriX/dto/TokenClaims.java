package com.bajriX.bajriX.dto;

public record TokenClaims(
    Long userId,
    String email,
    String role,
    Long sellerId,
    long exp
) {}
