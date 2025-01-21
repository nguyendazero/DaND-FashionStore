package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddTagRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctTagResponse;

import java.util.List;

public interface TagService {

    public APICustomize<ItsRctTagResponse> add(AddTagRequest request);

    public APICustomize<List<ItsRctTagResponse>> tags();

    public APICustomize<ItsRctTagResponse> tag(Long id);

    public APICustomize<String> delete(Long id);

}
