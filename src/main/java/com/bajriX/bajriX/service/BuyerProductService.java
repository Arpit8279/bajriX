package com.bajriX.bajriX.service;

import com.bajriX.bajriX.dto.response.*;
import com.bajriX.bajriX.entity.Product;
import com.bajriX.bajriX.entity.SellerListing;
import com.bajriX.bajriX.exception.ResourceNotFoundException;
import com.bajriX.bajriX.repository.ProductRepository;
import com.bajriX.bajriX.repository.ProductSpecification;
import com.bajriX.bajriX.repository.SellerListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Buyer-facing product service.
 * All queries automatically filter for active products and APPROVED sellers.
 */
@Service
@Transactional(readOnly = true)
public class BuyerProductService {

    private final ProductRepository productRepository;
    private final SellerListingRepository listingRepository;

    public BuyerProductService(ProductRepository productRepository,
                                SellerListingRepository listingRepository) {
        this.productRepository = productRepository;
        this.listingRepository = listingRepository;
    }

    /**
     * Browse / search products with optional filters.
     * Builds a dynamic Specification from the provided parameters.
     *
     * Returns product summaries with aggregated listing stats (lowest price, seller count).
     */
    public PagedResponse<ProductSummaryResponse> searchProducts(
            String query, Long categoryId, String brand, Pageable pageable) {

        // Build dynamic specification
        Specification<Product> spec = ProductSpecification.isActive();

        if (query != null && !query.isBlank()) {
            spec = spec.and(ProductSpecification.nameOrBrandContains(query.trim()));
        }
        if (categoryId != null) {
            spec = spec.and(ProductSpecification.hasCategory(categoryId));
        }
        if (brand != null && !brand.isBlank()) {
            spec = spec.and(ProductSpecification.hasBrand(brand.trim()));
        }

        // Step 1: Get paginated products
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        // Step 2: Batch-fetch listing stats to avoid N+1
        List<Long> productIds = productPage.getContent().stream()
                .map(Product::getId)
                .toList();

        Map<Long, BigDecimal> lowestPrices = new HashMap<>();
        Map<Long, Integer> sellerCounts = new HashMap<>();

        if (!productIds.isEmpty()) {
            List<Object[]> stats = productRepository.findListingStatsByProductIds(productIds);
            for (Object[] row : stats) {
                Long pid = (Long) row[0];
                lowestPrices.put(pid, (BigDecimal) row[1]);
                sellerCounts.put(pid, ((Long) row[2]).intValue());
            }
        }

        // Step 3: Map to response DTOs
        Page<ProductSummaryResponse> responsePage = productPage.map(p ->
            new ProductSummaryResponse(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getBrand(),
                p.getUnit(),
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getImageUrl(),
                lowestPrices.getOrDefault(p.getId(), null),
                sellerCounts.getOrDefault(p.getId(), 0)
            )
        );

        return PagedResponse.from(responsePage);
    }

    /**
     * Get full product detail with all seller offers.
     * Only shows listings from APPROVED sellers that are currently available.
     */
    public ProductDetailResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + slug));

        if (!product.getActive()) {
            throw new ResourceNotFoundException("Product is no longer available: " + slug);
        }

        List<SellerListing> listings = listingRepository.findActiveListingsForProduct(product.getId());

        List<SellerOfferResponse> offers = listings.stream()
                .map(sl -> new SellerOfferResponse(
                        sl.getId(),
                        sl.getSeller().getBusinessName(),
                        sl.getSeller().getCity(),
                        sl.getPrice(),
                        sl.getStockQuantity(),
                        sl.getMinOrderQuantity()
                ))
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getBrand(),
                product.getUnit(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getImageUrl(),
                offers
        );
    }

    /**
     * Get distinct brands for the filter sidebar.
     */
    public List<String> getDistinctBrands() {
        return productRepository.findDistinctBrands();
    }
}
