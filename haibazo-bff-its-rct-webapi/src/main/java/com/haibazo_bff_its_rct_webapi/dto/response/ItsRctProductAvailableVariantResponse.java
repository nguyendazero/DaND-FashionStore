package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctProductAvailableVariantResponse {
    private Long id;
    private String highLightedImageUrl;
    private BigDecimal price;
    private long stock;
    private Long productId;
    private ItsRctDiscountResponse discount;
    private List<ItsRctProductVariantResponse> productVariants;
}
