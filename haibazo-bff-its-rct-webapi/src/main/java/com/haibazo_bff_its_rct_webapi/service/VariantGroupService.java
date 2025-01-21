package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddVariantGroupRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctVariantGroupResponse;

import java.util.List;

public interface VariantGroupService {

    public APICustomize<List<ItsRctVariantGroupResponse>> variantGroups();

    public APICustomize<ItsRctVariantGroupResponse> variantGroup(String key);

    public APICustomize<ItsRctVariantGroupResponse> add(AddVariantGroupRequest request);

    public APICustomize<String> delete(String key);

}
