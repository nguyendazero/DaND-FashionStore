package com.haibazo_bff_its_rct_webapi.dto.response;
import com.haibazo_bff_its_rct_webapi.enums.Configs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctConfigResponse {
    private Long id;
    private Configs configKey;
    private String value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
