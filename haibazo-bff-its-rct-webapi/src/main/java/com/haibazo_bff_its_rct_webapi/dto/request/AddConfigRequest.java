package com.haibazo_bff_its_rct_webapi.dto.request;

import com.haibazo_bff_its_rct_webapi.enums.Configs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddConfigRequest {
    private Configs configKey;
    private String value;
}
