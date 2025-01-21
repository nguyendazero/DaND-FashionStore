package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddDiscountRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddReviewRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDiscountResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctReviewResponse;

import java.util.List;

public interface DiscountService {

    public APICustomize<ItsRctDiscountResponse> discount(Long id);

    public APICustomize<List<ItsRctDiscountResponse>> discounts();

    public APICustomize<ItsRctDiscountResponse> add(AddDiscountRequest request);

    public APICustomize<String> delete(Long id);

}
