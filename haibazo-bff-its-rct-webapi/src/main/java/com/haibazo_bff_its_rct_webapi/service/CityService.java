package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCityRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCityResponse;

import java.util.List;

public interface CityService {

    public APICustomize<ItsRctCityResponse> create(AddCityRequest request);

    public APICustomize<List<ItsRctCityResponse>> cities(Long stateId);

    public APICustomize<ItsRctCityResponse> city(Long id);

    public APICustomize<ItsRctCityResponse> update(Long id, AddCityRequest request);

    public APICustomize<String> delete(Long id);

}
