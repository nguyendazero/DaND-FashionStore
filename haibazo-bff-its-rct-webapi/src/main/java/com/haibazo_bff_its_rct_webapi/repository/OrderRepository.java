package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.Order;
import com.haibazo_bff_its_rct_webapi.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Query("SELECT COUNT(od) > 0 FROM Order o JOIN o.orderDetails od " +
            "WHERE o.user.id = :userId AND od.productAvailableVariant.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}
