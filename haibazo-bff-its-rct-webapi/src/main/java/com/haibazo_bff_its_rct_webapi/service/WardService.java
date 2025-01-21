package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddWardRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctWardResponse;

import java.util.List;

public interface WardService {

    public APICustomize<ItsRctWardResponse> create(AddWardRequest request);

    public APICustomize<ItsRctWardResponse> update(Long id, AddWardRequest request);

    public APICustomize<String> delete(Long id);

    public APICustomize<ItsRctWardResponse> ward(Long id);

    public APICustomize<List<ItsRctWardResponse>> wards(Long districtId);

}
