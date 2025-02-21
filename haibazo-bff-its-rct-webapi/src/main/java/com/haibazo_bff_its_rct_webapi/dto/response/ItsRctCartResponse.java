package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctCartResponse {
    private ItsRctProductAvailableVariantResponse productAvailableVariantResponse;
    private long quantity;
}
