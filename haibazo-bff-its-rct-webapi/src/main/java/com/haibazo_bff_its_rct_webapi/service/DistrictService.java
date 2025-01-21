package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddDistrictRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDistrictResponse;

import java.util.List;

public interface DistrictService {

    public APICustomize<ItsRctDistrictResponse> create(AddDistrictRequest request);

    public APICustomize<ItsRctDistrictResponse> district(Long id);

    public APICustomize<List<ItsRctDistrictResponse>> districts(Long cityId);

    public APICustomize<ItsRctDistrictResponse> update(Long id,AddDistrictRequest request);

    public APICustomize<String> delete(Long id);

}
