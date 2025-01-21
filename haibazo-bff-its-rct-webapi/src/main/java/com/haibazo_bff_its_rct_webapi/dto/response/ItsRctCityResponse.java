package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctCityResponse {
    private Long id;
    private String name;
    private Long stateId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
