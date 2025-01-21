package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAddressRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;

import java.util.List;

public interface AddressService{

    public APICustomize<ItsRctAddressResponse> create(AddAddressRequest request);

    public APICustomize<ItsRctAddressResponse> update(Long id, AddAddressRequest request);

    public APICustomize<List<ItsRctAddressResponse>> addresses(Long wardId);

    public APICustomize<ItsRctAddressResponse> address(Long id);

    public APICustomize<String> delete(Long id);

}
