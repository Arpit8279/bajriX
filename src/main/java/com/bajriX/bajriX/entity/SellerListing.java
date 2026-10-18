package com.bajriX.bajriX.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller_listings",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_seller_product",
           columnNames = {"seller_id", "product_id"}
       ))
@Getter
@Setter
@NoArgsConstructor
public class SellerListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "min_order_quantity", nullable = false)
    private Integer minOrderQuantity = 1;

    @Column(nullable = false)
    private Boolean available = true;

    /**
     * Optimistic locking version field.
     * Hibernate automatically adds "WHERE version = ?" on updates.
     * If two concurrent updates collide, the second throws OptimisticLockException.
     */
    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
