package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderDetailResponse;

import java.util.List;

public interface OrderDetailService {

    public APICustomize<List<ItsRctOrderDetailResponse>> orderDetails(Long orderId);

}
