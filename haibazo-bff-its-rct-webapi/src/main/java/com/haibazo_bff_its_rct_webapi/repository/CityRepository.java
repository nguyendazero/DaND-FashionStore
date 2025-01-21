package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.City;
import com.haibazo_bff_its_rct_webapi.model.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    boolean existsByName(String name);

    List<City> findAllByStateId(Long stateId);

}
