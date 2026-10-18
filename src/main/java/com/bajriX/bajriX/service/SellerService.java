package com.bajriX.bajriX.service;

import com.bajriX.bajriX.dto.response.SellerResponse;
import com.bajriX.bajriX.entity.Seller;
import com.bajriX.bajriX.exception.ResourceNotFoundException;
import com.bajriX.bajriX.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    /**
     * Get all sellers (for the seller switcher dropdown in the UI).
     */
    public List<SellerResponse> getAllSellers() {
        return sellerRepository.findAllByOrderByBusinessNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get a seller by ID (for profile page / validation).
     */
    public SellerResponse getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + id));
        return toResponse(seller);
    }

    private SellerResponse toResponse(Seller s) {
        return new SellerResponse(
                s.getId(), s.getBusinessName(), s.getSlug(),
                s.getContactEmail(), s.getContactPhone(),
                s.getAddress(), s.getCity(), s.getStatus().name()
        );
    }
}
