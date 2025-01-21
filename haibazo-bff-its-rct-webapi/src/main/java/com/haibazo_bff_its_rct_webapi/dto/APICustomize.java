package com.haibazo_bff_its_rct_webapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APICustomize<T> {
    private String statusCode;
    private  String message;
    private T result ;

}
