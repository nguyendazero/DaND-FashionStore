package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddLanguageRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctLanguageResponse;
import com.haibazo_bff_its_rct_webapi.model.Language;

import java.util.List;

public interface LanguageService {

    public APICustomize<List<ItsRctLanguageResponse>> languages();

    public APICustomize<ItsRctLanguageResponse> addLanguage(AddLanguageRequest request);

    public APICustomize<ItsRctLanguageResponse> getLanguageByCode(String code);

    public APICustomize<String> deleteByCode(String code);
}
