package com.bajriX.bajriX.dto.response;

import java.util.List;

/**
 * Full product detail with all seller offers — shown on product detail page.
 */
public record ProductDetailResponse(
    Long id,
    String name,
    String slug,
    String description,
    String brand,
    String unit,
    String categoryName,
    String imageUrl,
    List<SellerOfferResponse> sellers
) {}
