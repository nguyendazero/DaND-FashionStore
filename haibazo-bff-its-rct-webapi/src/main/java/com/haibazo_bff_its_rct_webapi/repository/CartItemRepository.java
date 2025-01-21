package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.model.CartItem;
import com.haibazo_bff_its_rct_webapi.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    boolean existsByUserIdAndProductAvailableVariantId(Long userId, Long productAvailableVariantId);

    List<CartItem> findByUserId(Long userId);

    CartItem findByUserIdAndProductAvailableVariantId(Long userId, Long productAvailableVariantId);
}
