package com.haibazo_bff_its_rct_webapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    private Long productAvailableVariantId;
    private long quantity;
}
