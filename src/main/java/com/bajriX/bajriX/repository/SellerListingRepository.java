package com.bajriX.bajriX.repository;

import com.bajriX.bajriX.entity.SellerListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerListingRepository extends JpaRepository<SellerListing, Long> {

    /**
     * Buyer view: get all active listings for a product from APPROVED sellers only.
     * Sorted by price ascending (cheapest first).
     */
    @Query("SELECT sl FROM SellerListing sl " +
           "JOIN FETCH sl.seller s " +
           "JOIN FETCH sl.product p " +
           "WHERE sl.product.id = :productId " +
           "AND sl.available = true " +
           "AND s.status = com.bajriX.bajriX.enums.SellerStatus.APPROVED " +
           "ORDER BY sl.price ASC")
    List<SellerListing> findActiveListingsForProduct(@Param("productId") Long productId);

    /**
     * Seller view: get paginated listings for a specific seller.
     */
    @Query("SELECT sl FROM SellerListing sl " +
           "JOIN FETCH sl.product p " +
           "LEFT JOIN FETCH p.category " +
           "WHERE sl.seller.id = :sellerId " +
           "ORDER BY sl.createdAt DESC")
    List<SellerListing> findBySellerIdWithProduct(@Param("sellerId") Long sellerId);

    /**
     * Find a listing that belongs to a specific seller (authorization scoping).
     * This ensures a seller can only access their own listing.
     */
    Optional<SellerListing> findByIdAndSellerId(Long id, Long sellerId);

    /**
     * Check if a seller already has a listing for this product.
     * Used to prevent duplicates before insert.
     */
    boolean existsBySellerIdAndProductId(Long sellerId, Long productId);
}
