package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctCartResponse {
    private Long userId;
    private ItsRctProductAvailableVariantResponse productAvailableVariantResponse;
    private long quantity;
}
