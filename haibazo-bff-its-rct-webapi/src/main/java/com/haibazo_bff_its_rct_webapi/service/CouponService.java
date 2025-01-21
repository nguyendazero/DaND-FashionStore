package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCouponRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCouponResponse;

import java.util.List;

public interface CouponService {

    public APICustomize<List<ItsRctCouponResponse>> coupons();

    public APICustomize<ItsRctCouponResponse> create(AddCouponRequest request);

    public APICustomize<ItsRctCouponResponse> coupon(Long id);

    public APICustomize<String> delete(Long id);

    public APICustomize<ItsRctCouponResponse> update(Long id, AddCouponRequest request);

}
