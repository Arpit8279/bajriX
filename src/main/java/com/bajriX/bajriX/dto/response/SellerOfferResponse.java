package com.bajriX.bajriX.dto.response;

import java.math.BigDecimal;

/**
 * A single seller's offer for a product — used inside ProductDetailResponse.
 */
public record SellerOfferResponse(
    Long listingId,
    String sellerName,
    String sellerCity,
    BigDecimal price,
    int stockQuantity,
    int minOrderQuantity
) {}
