package com.haibazo_bff_its_rct_webapi.dto.request;

import com.haibazo_bff_its_rct_webapi.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCouponRequest {
    private String code;
    private BigDecimal discount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minSpend;
    private CouponType type;
}
