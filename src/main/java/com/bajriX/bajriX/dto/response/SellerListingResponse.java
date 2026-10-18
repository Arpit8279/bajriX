package com.bajriX.bajriX.dto.response;

import java.math.BigDecimal;

/**
 * Seller's view of their own listing — includes version for optimistic locking.
 */
public record SellerListingResponse(
    Long id,
    Long productId,
    String productName,
    String productBrand,
    String productUnit,
    String categoryName,
    BigDecimal price,
    int stockQuantity,
    int minOrderQuantity,
    boolean available,
    int version
) {}
