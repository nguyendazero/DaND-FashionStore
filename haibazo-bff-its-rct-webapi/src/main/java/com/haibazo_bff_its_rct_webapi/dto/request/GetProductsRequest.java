package com.haibazo_bff_its_rct_webapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProductsRequest {
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String sortBy;
    private String orderBy;
    private String search;
    private List<Long> variantIds = new ArrayList<>();
    private List<Long> categoryIds = new ArrayList<>();
}
