package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCountryRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCountryResponse;

import java.util.List;

public interface CountryService {

    public APICustomize<List<ItsRctCountryResponse>> countries();

    public APICustomize<ItsRctCountryResponse> countryByCode(String code);

    public APICustomize<ItsRctCountryResponse> addCountry(AddCountryRequest request);

    public APICustomize<String> deleteCountry(String code);

    public APICustomize<ItsRctCountryResponse> updateCountry(String code, AddCountryRequest request);
}
