package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    List<WishList> findByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}
