package com.bajriX.bajriX.dto.response;

/**
 * Seller profile info — used in the seller switcher dropdown and profile page.
 */
public record SellerResponse(
    Long id,
    String businessName,
    String slug,
    String contactEmail,
    String contactPhone,
    String address,
    String city,
    String status
) {}
