package com.bajriX.bajriX.controller;

import com.bajriX.bajriX.dto.response.*;
import com.bajriX.bajriX.service.BuyerProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Buyer-facing endpoints — all public, no authentication required.
 */
@RestController
@RequestMapping("/api/buyer/products")
public class BuyerProductController {

    private final BuyerProductService buyerProductService;

    public BuyerProductController(BuyerProductService buyerProductService) {
        this.buyerProductService = buyerProductService;
    }

    /**
     * Browse / search products with filters.
     *
     * @param q          search query (matches product name or brand)
     * @param categoryId filter by category
     * @param brand      filter by brand
     * @param sortBy     sort field: "name", "brand" (default: "name")
     * @param direction  sort direction: "asc", "desc" (default: "asc")
     * @param page       page number (0-indexed, default: 0)
     * @param size       page size (default: 20, max: 100)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductSummaryResponse>>> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // Clamp page size to prevent abuse
        size = Math.min(size, 100);

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<ProductSummaryResponse> response =
                buyerProductService.searchProducts(q, categoryId, brand, pageable);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Get full product detail with all available seller offers.
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductBySlug(
            @PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(buyerProductService.getProductBySlug(slug)));
    }

    /**
     * Get distinct brands for the filter sidebar.
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<String>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.ok(buyerProductService.getDistinctBrands()));
    }
}
