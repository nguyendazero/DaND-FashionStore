package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.UserRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCouponResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.model.User;

import java.util.List;

public interface UserService {
    public APICustomize<ItsRctUserResponse> getUserById(Long id);

    public Long create(UserRequest request);

    public APICustomize<List<ItsRctCouponResponse>> couponsByUserId(Long id);

    public APICustomize<List<ItsRctProductResponse>> wishListByUserId(Long id);

    public APICustomize<List<ItsRctAddressResponse>> getAddressesByUserId(Long userId);

    public APICustomize<String> delete(Long id);
}
