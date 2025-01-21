package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddStateRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStateResponse;

import java.util.List;

public interface StateService {

    public APICustomize<ItsRctStateResponse> add(AddStateRequest request);

    public APICustomize<List<ItsRctStateResponse>> states(String countryCode);

    public APICustomize<ItsRctStateResponse> state(Long id);

    public APICustomize<String> delete(Long id);

    public APICustomize<ItsRctStateResponse> update(Long id, AddStateRequest request);

}
