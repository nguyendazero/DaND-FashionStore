package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAvailableVariantRepository extends JpaRepository<ProductAvailableVariant, Long> {

    List<ProductAvailableVariant> findByProductId(Long productId);

}
