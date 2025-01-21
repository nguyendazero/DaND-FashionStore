package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddConfigRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctConfigResponse;

import java.util.List;

public interface ConfigService {

    public APICustomize<ItsRctConfigResponse> add(AddConfigRequest request);

    public APICustomize<ItsRctConfigResponse> update(Long id, AddConfigRequest request);

    public APICustomize<ItsRctConfigResponse> config(Long id);

    public APICustomize<List<ItsRctConfigResponse>> configs();

    public APICustomize<String> delete(Long id);

}
