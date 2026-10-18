package com.bajriX.bajriX.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateListingRequest(

    @NotNull(message = "Product ID is required")
    Long productId,

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    BigDecimal price,

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    Integer stockQuantity,

    @NotNull(message = "Minimum order quantity is required")
    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    Integer minOrderQuantity
) {}
