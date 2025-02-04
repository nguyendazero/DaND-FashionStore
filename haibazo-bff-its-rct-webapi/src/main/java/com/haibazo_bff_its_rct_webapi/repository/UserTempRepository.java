package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserTempRepository extends JpaRepository<UserTemp, Long> {
    List<UserTemp> findAllByCreatedAtBefore(LocalDateTime dateTime);
}
