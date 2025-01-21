package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctOrderDetailResponse {
    private Long id;
    private Long orderId;
    private ItsRctProductAvailableVariantResponse productAvailableVariant;
    private long quantity;
    private LocalDateTime createdAt;
}
