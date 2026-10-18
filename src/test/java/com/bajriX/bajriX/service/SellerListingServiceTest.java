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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SellerListingService — the core business logic.
 *
 * Tests focus on:
 * - Business rule enforcement
 * - Authorization boundaries (seller can't access other seller's listings)
 * - Edge cases (zero stock, duplicate listings)
 */
@ExtendWith(MockitoExtension.class)
class SellerListingServiceTest {

    @Mock private SellerListingRepository listingRepository;
    @Mock private ProductRepository productRepository;
    @Mock private SellerRepository sellerRepository;

    @InjectMocks private SellerListingService service;

    private Seller testSeller;
    private Product testProduct;
    private SellerListing testListing;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setBusinessName("Test Seller");
        testSeller.setStatus(SellerStatus.APPROVED);

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setName("UltraTech PPC Cement 50 kg");
        testProduct.setBrand("UltraTech");
        testProduct.setUnit("bag");
        testProduct.setActive(true);

        testListing = new SellerListing();
        testListing.setId(100L);
        testListing.setSeller(testSeller);
        testListing.setProduct(testProduct);
        testListing.setPrice(new BigDecimal("390.00"));
        testListing.setStockQuantity(500);
        testListing.setMinOrderQuantity(10);
        testListing.setAvailable(true);
        testListing.setVersion(0);
    }

    // =========== CREATE LISTING TESTS ===========

    @Nested
    @DisplayName("createListing")
    class CreateListingTests {

        @Test
        @DisplayName("should create listing successfully")
        void shouldCreateListing() {
            when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
            when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
            when(listingRepository.existsBySellerIdAndProductId(1L, 10L)).thenReturn(false);
            when(listingRepository.save(any())).thenAnswer(inv -> {
                SellerListing saved = inv.getArgument(0);
                saved.setId(100L);
                return saved;
            });

            CreateListingRequest request = new CreateListingRequest(
                    10L, new BigDecimal("390.00"), 500, 10);

            SellerListingResponse response = service.createListing(1L, request);

            assertThat(response.productName()).isEqualTo("UltraTech PPC Cement 50 kg");
            assertThat(response.price()).isEqualByComparingTo(new BigDecimal("390.00"));
            assertThat(response.available()).isTrue();
            verify(listingRepository).save(any());
        }

        @Test
        @DisplayName("should fail when product does not exist")
        void shouldFailWhenProductNotFound() {
            when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            CreateListingRequest request = new CreateListingRequest(
                    999L, new BigDecimal("100.00"), 100, 10);

            assertThatThrownBy(() -> service.createListing(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found");
        }

        @Test
        @DisplayName("should fail when product is inactive")
        void shouldFailWhenProductInactive() {
            testProduct.setActive(false);
            when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
            when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

            CreateListingRequest request = new CreateListingRequest(
                    10L, new BigDecimal("390.00"), 500, 10);

            assertThatThrownBy(() -> service.createListing(1L, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("inactive");
        }

        @Test
        @DisplayName("should fail when duplicate listing exists")
        void shouldFailOnDuplicate() {
            when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
            when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
            when(listingRepository.existsBySellerIdAndProductId(1L, 10L)).thenReturn(true);

            CreateListingRequest request = new CreateListingRequest(
                    10L, new BigDecimal("390.00"), 500, 10);

            assertThatThrownBy(() -> service.createListing(1L, request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already have a listing");
        }

        @Test
        @DisplayName("should fail when MOQ exceeds stock")
        void shouldFailWhenMoqExceedsStock() {
            when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
            when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
            when(listingRepository.existsBySellerIdAndProductId(1L, 10L)).thenReturn(false);

            CreateListingRequest request = new CreateListingRequest(
                    10L, new BigDecimal("390.00"), 5, 10); // MOQ 10 > stock 5

            assertThatThrownBy(() -> service.createListing(1L, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("cannot exceed available stock");
        }

        @Test
        @DisplayName("should auto-mark unavailable when stock is zero")
        void shouldAutoMarkUnavailableOnZeroStock() {
            when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
            when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
            when(listingRepository.existsBySellerIdAndProductId(1L, 10L)).thenReturn(false);
            when(listingRepository.save(any())).thenAnswer(inv -> {
                SellerListing saved = inv.getArgument(0);
                saved.setId(100L);
                return saved;
            });

            CreateListingRequest request = new CreateListingRequest(
                    10L, new BigDecimal("390.00"), 0, 1);

            SellerListingResponse response = service.createListing(1L, request);

            assertThat(response.available()).isFalse();
        }

        @Test
        @DisplayName("should fail when seller does not exist")
        void shouldFailWhenSellerNotFound() {
            when(sellerRepository.findById(999L)).thenReturn(Optional.empty());

            CreateListingRequest request = new CreateListingRequest(
                    10L, new BigDecimal("100.00"), 100, 10);

            assertThatThrownBy(() -> service.createListing(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Seller not found");
        }
    }

    // =========== UPDATE LISTING TESTS ===========

    @Nested
    @DisplayName("updateListing")
    class UpdateListingTests {

        @Test
        @DisplayName("should update listing successfully")
        void shouldUpdateListing() {
            when(listingRepository.findByIdAndSellerId(100L, 1L))
                    .thenReturn(Optional.of(testListing));
            when(listingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UpdateListingRequest request = new UpdateListingRequest(
                    new BigDecimal("400.00"), 300, 5);

            SellerListingResponse response = service.updateListing(1L, 100L, request);

            assertThat(response.price()).isEqualByComparingTo(new BigDecimal("400.00"));
            assertThat(response.stockQuantity()).isEqualTo(300);
        }

        @Test
        @DisplayName("should fail when listing belongs to another seller")
        void shouldFailForAnotherSellersListing() {
            when(listingRepository.findByIdAndSellerId(100L, 2L))
                    .thenReturn(Optional.empty()); // Seller 2 cannot find seller 1's listing

            UpdateListingRequest request = new UpdateListingRequest(
                    new BigDecimal("400.00"), 300, 5);

            assertThatThrownBy(() -> service.updateListing(2L, 100L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found or you don't have permission");
        }

        @Test
        @DisplayName("should auto-mark unavailable when stock updated to zero")
        void shouldAutoDisableOnZeroStock() {
            when(listingRepository.findByIdAndSellerId(100L, 1L))
                    .thenReturn(Optional.of(testListing));
            when(listingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UpdateListingRequest request = new UpdateListingRequest(
                    new BigDecimal("390.00"), 0, 1);

            SellerListingResponse response = service.updateListing(1L, 100L, request);

            assertThat(response.available()).isFalse();
        }
    }

    // =========== TOGGLE AVAILABILITY TESTS ===========

    @Nested
    @DisplayName("toggleAvailability")
    class ToggleAvailabilityTests {

        @Test
        @DisplayName("should toggle from available to unavailable")
        void shouldToggleOff() {
            testListing.setAvailable(true);
            when(listingRepository.findByIdAndSellerId(100L, 1L))
                    .thenReturn(Optional.of(testListing));
            when(listingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            SellerListingResponse response = service.toggleAvailability(1L, 100L);

            assertThat(response.available()).isFalse();
        }

        @Test
        @DisplayName("should fail to activate when stock is zero")
        void shouldFailToActivateWithZeroStock() {
            testListing.setAvailable(false);
            testListing.setStockQuantity(0);
            when(listingRepository.findByIdAndSellerId(100L, 1L))
                    .thenReturn(Optional.of(testListing));

            assertThatThrownBy(() -> service.toggleAvailability(1L, 100L))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("stock is zero");
        }
    }

    // =========== DELETE LISTING TESTS ===========

    @Nested
    @DisplayName("deleteListing")
    class DeleteListingTests {

        @Test
        @DisplayName("should delete own listing")
        void shouldDeleteOwnListing() {
            when(listingRepository.findByIdAndSellerId(100L, 1L))
                    .thenReturn(Optional.of(testListing));

            service.deleteListing(1L, 100L);

            verify(listingRepository).delete(testListing);
        }

        @Test
        @DisplayName("should fail to delete another seller's listing")
        void shouldFailToDeleteOtherSellersListing() {
            when(listingRepository.findByIdAndSellerId(100L, 2L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteListing(2L, 100L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
