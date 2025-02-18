package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductAvailableVariantRepository extends JpaRepository<ProductAvailableVariant, Long> {

    List<ProductAvailableVariant> findByProductId(Long productId);

    @Query("SELECT pav FROM ProductAvailableVariant pav " +
            "JOIN pav.productVariants pvColor " +
            "JOIN pav.productVariants pvSize " +
            "WHERE pvColor.value = :color " +
            "AND pvColor.variantGroupKey.variantKey = 'color' " +
            "AND pvSize.value = :size " +
            "AND pvSize.variantGroupKey.variantKey = 'size' " +
            "AND pav.product.id = :productId")
    ProductAvailableVariant findByColorAndSizeAndProductId(@Param("color") String color, @Param("size") String size, @Param("productId") Long productId);
}
