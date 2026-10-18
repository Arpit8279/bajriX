package com.bajriX.bajriX.dto.response;

import java.math.BigDecimal;

/**
 * Product card shown in the buyer's browse/search page.
 * Includes aggregated listing stats (lowest price, seller count).
 */
public record ProductSummaryResponse(
    Long id,
    String name,
    String slug,
    String brand,
    String unit,
    String categoryName,
    String imageUrl,
    BigDecimal lowestPrice,
    int sellerCount
) {}
