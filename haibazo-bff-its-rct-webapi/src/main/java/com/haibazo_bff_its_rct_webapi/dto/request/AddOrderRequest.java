package com.haibazo_bff_its_rct_webapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderRequest {
    private String note;
    private Long addressId;
    private Long paymentId;
    private String couponCode;
}
