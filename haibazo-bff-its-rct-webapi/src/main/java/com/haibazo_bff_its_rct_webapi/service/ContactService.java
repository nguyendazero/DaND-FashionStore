package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddContactRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctContactResponse;

import java.util.List;

public interface ContactService {

    public APICustomize<ItsRctContactResponse> add(AddContactRequest request);

    public APICustomize<List<ItsRctContactResponse>> contacts();

    public APICustomize<ItsRctContactResponse> contact(Long id);

    public APICustomize<String> delete(Long id);

}
