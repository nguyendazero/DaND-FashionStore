package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductAvailableVariantRepository extends JpaRepository<ProductAvailableVariant, Long> {

    List<ProductAvailableVariant> findByProductId(Long productId);

    @Query("SELECT pav FROM ProductAvailableVariant pav JOIN pav.productVariants pv WHERE pv.value = :value AND pav.product.id = :productId")
    List<ProductAvailableVariant> findByProductVariantValueAndProductId(@Param("value") String value, @Param("productId") Long productId);

}
