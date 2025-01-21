package com.haibazo_bff_its_rct_webapi.dto.response;

import com.haibazo_bff_its_rct_webapi.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctOrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String note;
    private Long couponId;
    private Long paymentId;
    private Long addressId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
