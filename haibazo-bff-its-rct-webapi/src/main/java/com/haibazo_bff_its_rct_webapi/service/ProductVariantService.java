package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductVariantRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductVariantResponse;

import java.util.List;

public interface ProductVariantService {

    public APICustomize<List<ItsRctProductVariantResponse>> productVariants(Long productAvailableVariantId);

    public APICustomize<ItsRctProductVariantResponse> addProductVariant(Long productAvailableVariantId, AddProductVariantRequest request);

}
