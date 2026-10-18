package com.bajriX.bajriX.repository;

import com.bajriX.bajriX.entity.Product;
import org.springframework.data.jpa.domain.Specification;

/**
 * Composable query predicates for dynamic product search & filtering.
 *
 * Usage:
 *   Specification<Product> spec = ProductSpecification.isActive()
 *       .and(ProductSpecification.nameOrBrandContains("cement"))
 *       .and(ProductSpecification.hasCategory(1L));
 *   productRepository.findAll(spec, pageable);
 */
public class ProductSpecification {

    private ProductSpecification() {} // utility class

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<Product> nameOrBrandContains(String searchQuery) {
        return (root, query, cb) -> {
            String pattern = "%" + searchQuery.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("brand")), pattern)
            );
        };
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) -> cb.equal(root.get("brand"), brand);
    }
}
