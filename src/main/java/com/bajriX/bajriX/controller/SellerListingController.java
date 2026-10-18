package com.bajriX.bajriX.controller;

import com.bajriX.bajriX.dto.request.CreateListingRequest;
import com.bajriX.bajriX.dto.request.UpdateListingRequest;
import com.bajriX.bajriX.dto.response.ApiResponse;
import com.bajriX.bajriX.dto.response.SellerListingResponse;
import com.bajriX.bajriX.service.SellerListingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Seller-facing endpoints — require X-Seller-Id header for authorization.
 *
 * The sellerId is extracted from the header (mocked auth).
 * In production, this would come from a JWT token or session.
 */
@RestController
@RequestMapping("/api/seller/listings")
public class SellerListingController {

    private final SellerListingService listingService;

    public SellerListingController(SellerListingService listingService) {
        this.listingService = listingService;
    }

    /**
     * Get all my listings.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerListingResponse>>> getMyListings(
            @RequestHeader("X-Seller-Id") Long sellerId) {
        return ResponseEntity.ok(ApiResponse.ok(listingService.getMyListings(sellerId)));
    }

    /**
     * Get a single listing.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SellerListingResponse>> getMyListing(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(listingService.getMyListing(sellerId, id)));
    }

    /**
     * Add a product to my catalogue.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SellerListingResponse>> createListing(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @Valid @RequestBody CreateListingRequest request) {
        SellerListingResponse created = listingService.createListing(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Listing created successfully"));
    }

    /**
     * Update listing price, stock, and MOQ.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SellerListingResponse>> updateListing(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateListingRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(listingService.updateListing(sellerId, id, request),
                        "Listing updated successfully"));
    }

    /**
     * Toggle listing availability (quick on/off).
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<SellerListingResponse>> toggleAvailability(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(listingService.toggleAvailability(sellerId, id)));
    }

    /**
     * Remove a listing.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteListing(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long id) {
        listingService.deleteListing(sellerId, id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Listing deleted successfully"));
    }
}
