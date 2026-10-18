package com.bajriX.bajriX.repository;

import com.bajriX.bajriX.entity.Seller;
import com.bajriX.bajriX.enums.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    List<Seller> findAllByOrderByBusinessNameAsc();

    List<Seller> findByStatusOrderByBusinessNameAsc(SellerStatus status);
}
