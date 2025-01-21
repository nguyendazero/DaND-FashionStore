package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddStyleRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStyleResponse;

import java.util.List;

public interface StyleService {

    public APICustomize<List<ItsRctStyleResponse>> styles();

    public APICustomize<ItsRctStyleResponse> style(Long id);

    public APICustomize<ItsRctStyleResponse> add(AddStyleRequest request);

    public APICustomize<String> delete(Long id);

    public APICustomize<ItsRctStyleResponse> update(Long id, AddStyleRequest request);

}
