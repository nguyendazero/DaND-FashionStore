package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.CollectionProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionProductRepository extends JpaRepository<CollectionProduct, Long> {
    boolean existsByProductIdAndCollectionId(Long productId, Long collectionId);
}
