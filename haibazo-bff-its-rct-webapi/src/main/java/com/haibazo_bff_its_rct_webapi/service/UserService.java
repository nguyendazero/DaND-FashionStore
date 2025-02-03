package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.UpdateInfoRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.UserRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCouponResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;

import java.util.List;

public interface UserService {
    public APICustomize<ItsRctUserResponse> getUserByToken(String authorizationHeader);

    public Long create(UserRequest request);

    public APICustomize<List<ItsRctCouponResponse>> couponsByToken(String authorizationHeader) ;

    public APICustomize<List<ItsRctProductResponse>> wishListByToken(String authorizationHeader);

    public APICustomize<List<ItsRctAddressResponse>> getAddressesByToken(String authorizationHeader);

    public APICustomize<String> delete(Long id, String authorizationHeader);

    public APICustomize<ItsRctUserResponse> updateUserInfo(String authorizationHeader, UpdateInfoRequest request);
}
