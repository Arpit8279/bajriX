package com.bajriX.bajriX.repository;

import com.bajriX.bajriX.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
                                           JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    /**
     * Search active products by name or brand (case-insensitive).
     * Used as a fallback when Specification-based search is not needed.
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchByNameOrBrand(@Param("query") String query, Pageable pageable);

    /**
     * Fetch listing statistics (min price + seller count) for a batch of product IDs.
     * Only considers available listings from APPROVED sellers.
     * Returns Object[] = {productId, minPrice, sellerCount}.
     *
     * This avoids N+1 queries when building product summary cards.
     */
    @Query("SELECT sl.product.id, MIN(sl.price), COUNT(DISTINCT sl.seller.id) " +
           "FROM SellerListing sl " +
           "WHERE sl.product.id IN :productIds " +
           "AND sl.available = true " +
           "AND sl.seller.status = com.bajriX.bajriX.enums.SellerStatus.APPROVED " +
           "GROUP BY sl.product.id")
    List<Object[]> findListingStatsByProductIds(@Param("productIds") List<Long> productIds);

    /**
     * Get distinct brands across all active products (for filter UI).
     */
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true AND p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findDistinctBrands();
}
