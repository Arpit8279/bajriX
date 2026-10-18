package com.bajriX.bajriX.controller;

import com.bajriX.bajriX.dto.response.ApiResponse;
import com.bajriX.bajriX.dto.response.SellerResponse;
import com.bajriX.bajriX.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * List all sellers — used by the seller-switcher dropdown in the UI.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerResponse>>> getAllSellers() {
        return ResponseEntity.ok(ApiResponse.ok(sellerService.getAllSellers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SellerResponse>> getSellerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(sellerService.getSellerById(id)));
    }
}
