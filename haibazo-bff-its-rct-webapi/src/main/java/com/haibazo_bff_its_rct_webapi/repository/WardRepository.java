package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.District;
import com.haibazo_bff_its_rct_webapi.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, Long> {

    boolean existsByName(String name);

    List<Ward> findAllByDistrictId(Long districtId);

}
