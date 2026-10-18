package com.bajriX.bajriX.controller;

import com.bajriX.bajriX.dto.request.CreateListingRequest;
import com.bajriX.bajriX.dto.response.SellerListingResponse;
import com.bajriX.bajriX.exception.DuplicateResourceException;
import com.bajriX.bajriX.exception.GlobalExceptionHandler;
import com.bajriX.bajriX.exception.ResourceNotFoundException;
import com.bajriX.bajriX.service.SellerListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests — verify HTTP contract, validation, and error responses.
 * Uses standalone MockMvc setup with Mockito for fast, lightweight testing.
 */
@ExtendWith(MockitoExtension.class)
class SellerListingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SellerListingService listingService;

    @InjectMocks
    private SellerListingController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/seller/listings — returns 400 when X-Seller-Id is missing")
    void shouldReturn400WhenSellerIdMissing() throws Exception {
        mockMvc.perform(get("/api/seller/listings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/seller/listings — returns listings for seller")
    void shouldReturnListingsForSeller() throws Exception {
        SellerListingResponse listing = new SellerListingResponse(
                1L, 10L, "UltraTech PPC", "UltraTech", "bag", "Cement",
                new BigDecimal("390.00"), 500, 10, true, 0);

        when(listingService.getMyListings(1L)).thenReturn(List.of(listing));

        mockMvc.perform(get("/api/seller/listings")
                        .header("X-Seller-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].productName").value("UltraTech PPC"));
    }

    @Test
    @DisplayName("POST /api/seller/listings — returns 400 for invalid request body")
    void shouldReturn400ForInvalidBody() throws Exception {
        // Missing required fields, negative price
        String invalidBody = """
            {
                "productId": null,
                "price": -100,
                "stockQuantity": -5,
                "minOrderQuantity": 0
            }
            """;

        mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/seller/listings — returns 201 for valid request")
    void shouldReturn201ForValidRequest() throws Exception {
        SellerListingResponse created = new SellerListingResponse(
                1L, 10L, "UltraTech PPC", "UltraTech", "bag", "Cement",
                new BigDecimal("390.00"), 500, 10, true, 0);

        when(listingService.createListing(eq(1L), any(CreateListingRequest.class)))
                .thenReturn(created);

        String body = """
            {
                "productId": 10,
                "price": 390.00,
                "stockQuantity": 500,
                "minOrderQuantity": 10
            }
            """;

        mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("UltraTech PPC"));
    }

    @Test
    @DisplayName("POST /api/seller/listings — returns 409 for duplicate listing")
    void shouldReturn409ForDuplicate() throws Exception {
        when(listingService.createListing(eq(1L), any()))
                .thenThrow(new DuplicateResourceException("You already have a listing for this product"));

        String body = """
            {
                "productId": 10,
                "price": 390.00,
                "stockQuantity": 500,
                "minOrderQuantity": 10
            }
            """;

        mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("You already have a listing for this product"));
    }

    @Test
    @DisplayName("PUT /api/seller/listings/{id} — returns 404 for another seller's listing")
    void shouldReturn404ForOtherSellersListing() throws Exception {
        when(listingService.updateListing(eq(2L), eq(100L), any()))
                .thenThrow(new ResourceNotFoundException("Listing not found or you don't have permission"));

        String body = """
            {"price": 400, "stockQuantity": 300, "minOrderQuantity": 5}
            """;

        mockMvc.perform(put("/api/seller/listings/100")
                        .header("X-Seller-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("DELETE /api/seller/listings/{id} — returns 200 on successful delete")
    void shouldReturn200OnDelete() throws Exception {
        mockMvc.perform(delete("/api/seller/listings/100")
                        .header("X-Seller-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
