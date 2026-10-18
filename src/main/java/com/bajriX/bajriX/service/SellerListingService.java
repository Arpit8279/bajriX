package com.bajriX.bajriX.service;

import com.bajriX.bajriX.dto.request.CreateListingRequest;
import com.bajriX.bajriX.dto.request.UpdateListingRequest;
import com.bajriX.bajriX.dto.response.SellerListingResponse;
import com.bajriX.bajriX.entity.Product;
import com.bajriX.bajriX.entity.Seller;
import com.bajriX.bajriX.entity.SellerListing;
import com.bajriX.bajriX.enums.SellerStatus;
import com.bajriX.bajriX.exception.BusinessRuleException;
import com.bajriX.bajriX.exception.DuplicateResourceException;
import com.bajriX.bajriX.exception.ResourceNotFoundException;
import com.bajriX.bajriX.repository.ProductRepository;
import com.bajriX.bajriX.repository.SellerListingRepository;
import com.bajriX.bajriX.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Core service for seller listing management.
 *
 * AUTHORIZATION: Every method takes sellerId as a parameter and scopes all queries by it.
 * There is no code path where a seller can access another seller's listing.
 */
@Service
public class SellerListingService {

    private final SellerListingRepository listingRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    public SellerListingService(SellerListingRepository listingRepository,
                                 ProductRepository productRepository,
                                 SellerRepository sellerRepository) {
        this.listingRepository = listingRepository;
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }

    /**
     * Get all listings for a seller.
     */
    @Transactional(readOnly = true)
    public List<SellerListingResponse> getMyListings(Long sellerId) {
        validateSellerExists(sellerId);
        return listingRepository.findBySellerIdWithProduct(sellerId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get a single listing — scoped to the seller.
     */
    @Transactional(readOnly = true)
    public SellerListingResponse getMyListing(Long sellerId, Long listingId) {
        SellerListing listing = findListingForSeller(sellerId, listingId);
        return toResponse(listing);
    }

    /**
     * Add a new product to the seller's catalogue.
     *
     * Business rules:
     * - Product must exist and be active
     * - Seller must exist
     * - Seller cannot list the same product twice
     * - MOQ must not exceed stock (if stock > 0)
     */
    @Transactional
    public SellerListingResponse createListing(Long sellerId, CreateListingRequest request) {
        // Validate seller
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + sellerId));

        // Validate product
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.productId()));

        if (!product.getActive()) {
            throw new BusinessRuleException("Cannot list an inactive product: " + product.getName());
        }

        // Check for duplicate listing
        if (listingRepository.existsBySellerIdAndProductId(sellerId, request.productId())) {
            throw new DuplicateResourceException(
                    "You already have a listing for product: " + product.getName());
        }

        // Validate business rules
        validateMinOrderQuantity(request.minOrderQuantity(), request.stockQuantity());

        // Create listing
        SellerListing listing = new SellerListing();
        listing.setSeller(seller);
        listing.setProduct(product);
        listing.setPrice(request.price());
        listing.setStockQuantity(request.stockQuantity());
        listing.setMinOrderQuantity(request.minOrderQuantity());
        // Auto-set availability based on stock
        listing.setAvailable(request.stockQuantity() > 0);

        listing = listingRepository.save(listing);
        return toResponse(listing);
    }

    /**
     * Update an existing listing's price, stock, and MOQ.
     *
     * Optimistic locking: If another process updated this listing since the seller
     * last read it, JPA throws OptimisticLockException → GlobalExceptionHandler
     * returns 409 Conflict.
     */
    @Transactional
    public SellerListingResponse updateListing(Long sellerId, Long listingId, UpdateListingRequest request) {
        SellerListing listing = findListingForSeller(sellerId, listingId);

        validateMinOrderQuantity(request.minOrderQuantity(), request.stockQuantity());

        listing.setPrice(request.price());
        listing.setStockQuantity(request.stockQuantity());
        listing.setMinOrderQuantity(request.minOrderQuantity());

        // Auto-mark unavailable if stock hits 0
        if (request.stockQuantity() == 0) {
            listing.setAvailable(false);
        }

        listing = listingRepository.save(listing);
        return toResponse(listing);
    }

    /**
     * Toggle listing availability (quick on/off without editing the full listing).
     */
    @Transactional
    public SellerListingResponse toggleAvailability(Long sellerId, Long listingId) {
        SellerListing listing = findListingForSeller(sellerId, listingId);

        // Can't make available if stock is 0
        if (!listing.getAvailable() && listing.getStockQuantity() == 0) {
            throw new BusinessRuleException(
                    "Cannot make listing available when stock is zero. Please update stock first.");
        }

        listing.setAvailable(!listing.getAvailable());
        listing = listingRepository.save(listing);
        return toResponse(listing);
    }

    /**
     * Remove a listing (hard delete).
     */
    @Transactional
    public void deleteListing(Long sellerId, Long listingId) {
        SellerListing listing = findListingForSeller(sellerId, listingId);
        listingRepository.delete(listing);
    }

    // ---- Private helpers ----

    /**
     * Core authorization method: finds a listing that belongs to the given seller.
     * If the listing doesn't exist OR belongs to a different seller → 404.
     */
    private SellerListing findListingForSeller(Long sellerId, Long listingId) {
        return listingRepository.findByIdAndSellerId(listingId, sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Listing not found or you don't have permission to access it"));
    }

    private void validateSellerExists(Long sellerId) {
        if (!sellerRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("Seller not found with ID: " + sellerId);
        }
    }

    private void validateMinOrderQuantity(int moq, int stock) {
        if (stock > 0 && moq > stock) {
            throw new BusinessRuleException(
                    "Minimum order quantity (" + moq + ") cannot exceed available stock (" + stock + ")");
        }
    }

    private SellerListingResponse toResponse(SellerListing sl) {
        Product p = sl.getProduct();
        return new SellerListingResponse(
                sl.getId(),
                p.getId(),
                p.getName(),
                p.getBrand(),
                p.getUnit(),
                p.getCategory() != null ? p.getCategory().getName() : null,
                sl.getPrice(),
                sl.getStockQuantity(),
                sl.getMinOrderQuantity(),
                sl.getAvailable(),
                sl.getVersion()
        );
    }
}
