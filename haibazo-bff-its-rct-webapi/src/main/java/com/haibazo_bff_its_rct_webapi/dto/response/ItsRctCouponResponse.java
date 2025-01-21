package com.haibazo_bff_its_rct_webapi.dto.response;

import com.haibazo_bff_its_rct_webapi.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctCouponResponse {
    private Long id;
    private String code;
    private CouponType type;
    private LocalDateTime startDate;
    private BigDecimal discount;
    private LocalDateTime endDate;
    private BigDecimal minSpend;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
