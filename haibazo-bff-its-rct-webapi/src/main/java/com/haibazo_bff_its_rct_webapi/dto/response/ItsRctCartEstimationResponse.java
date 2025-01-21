package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctCartEstimationResponse {
    private BigDecimal total;
    private long totalQuantity;
    private ItsRctCouponResponse coupon;
    private List<ItsRctCartResponse> carts = new ArrayList<>();
}
