package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddOrderRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderResponse;
import com.haibazo_bff_its_rct_webapi.model.User;

import java.util.List;

public interface OrderService {

    public APICustomize<ItsRctOrderResponse> create(AddOrderRequest request);

    public APICustomize<List<ItsRctOrderResponse>> getOrdersByUserId(Long userId);

}
