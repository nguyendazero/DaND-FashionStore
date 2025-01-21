package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.model.Image;
import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import com.haibazo_bff_its_rct_webapi.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductAvailableVariant(ProductAvailableVariant productAvailableVariant);

}
