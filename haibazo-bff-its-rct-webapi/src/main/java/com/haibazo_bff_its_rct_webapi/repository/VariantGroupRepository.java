package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.VariantGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VariantGroupRepository extends JpaRepository<VariantGroup, String> {

    Optional<VariantGroup> findByVariantKey(String key);

    boolean existsByName(String name);

    boolean existsByVariantKey(String variantKey);

}
