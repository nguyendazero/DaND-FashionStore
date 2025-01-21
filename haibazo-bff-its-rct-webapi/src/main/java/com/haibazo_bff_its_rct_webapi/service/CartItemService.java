package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddToCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;

import java.util.List;

public interface CartItemService {

    public APICustomize<List<ItsRctCartResponse>> getCartItemByUserid(Long userId);

    public APICustomize<String> addToCart(AddToCartRequest request);

    public APICustomize<String> removeFromCart(RemoveFromCartRequest request);


}
