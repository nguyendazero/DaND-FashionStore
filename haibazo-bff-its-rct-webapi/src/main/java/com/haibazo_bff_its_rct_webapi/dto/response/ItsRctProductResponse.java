package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctProductResponse {
    private Long id;
    private String name;
    private String weigh;
    private BigDecimal rating;
    private long viewCount;
    private String sortDescription;
    private String description;
    private String introduction;
    private String highLightedImageUrl;
    private List<ItsRctImageResponse> images;
    private List<ItsRctProductAvailableVariantResponse> availableVariants;
    private ItsRctCategoryResponse category;
    private ItsRctStyleResponse style;
    // co the null
    private ItsRctDiscountResponse discount;
    private BigDecimal lowestPrice;
}
