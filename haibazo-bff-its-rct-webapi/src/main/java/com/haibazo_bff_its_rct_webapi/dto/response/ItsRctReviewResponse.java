package com.haibazo_bff_its_rct_webapi.dto.response;

import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctReviewResponse {
    private Long id;
    private String content;
    private BigDecimal stars;
    private List<ItsRctImageResponse> images;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
