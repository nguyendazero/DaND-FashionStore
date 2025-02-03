package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsResponse {
    private int month;
    private int year;
    private int totalOrders;
    private BigDecimal totalRevenue;
}
