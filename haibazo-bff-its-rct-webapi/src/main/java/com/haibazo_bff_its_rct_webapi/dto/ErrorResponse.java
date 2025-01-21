package com.haibazo_bff_its_rct_webapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String timestamp;
    private String path;
    private String debugMessage;
    private List<ErrorDetail> errors;
}
