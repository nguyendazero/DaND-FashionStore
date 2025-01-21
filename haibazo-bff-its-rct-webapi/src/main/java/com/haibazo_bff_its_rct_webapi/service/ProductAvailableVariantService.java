package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductAvailableVariantRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductAvailableVariantResponse;

import java.util.List;

public interface ProductAvailableVariantService {

    public APICustomize<List<ItsRctProductAvailableVariantResponse>> productAvailableVariants(Long productId);

    public APICustomize<ItsRctProductAvailableVariantResponse> productAvailableVariant(Long id);

    public APICustomize<ItsRctProductAvailableVariantResponse> add(Long productId, AddProductAvailableVariantRequest request);

    public APICustomize<ItsRctProductAvailableVariantResponse> update(Long id, AddProductAvailableVariantRequest request);

    public APICustomize<String> delete(Long id);

}
