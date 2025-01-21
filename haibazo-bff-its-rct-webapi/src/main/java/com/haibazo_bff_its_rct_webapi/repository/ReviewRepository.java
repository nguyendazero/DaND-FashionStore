package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.Product;
import com.haibazo_bff_its_rct_webapi.model.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByProduct(Product product);

    @Query("SELECT AVG(r.stars) FROM Review r WHERE r.product = :product")
    BigDecimal findAverageStarsByProduct(@Param("product") Product product);
}
