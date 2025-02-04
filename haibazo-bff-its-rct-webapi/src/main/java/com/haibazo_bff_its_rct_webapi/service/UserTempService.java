package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.UserTempResponse;

import java.util.List;

public interface UserTempService {
    
    public APICustomize<List<UserTempResponse>> guests(String authorizationHeader);

    public APICustomize<UserTempResponse> guest(Long id, String authorizationHeader);
    
    public APICustomize<String> delete(Long id, String authorizationHeader);
    
}
