package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCurrencyRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCurrencyResponse;

import java.util.List;

public interface CurrencyService {
    public APICustomize<List<ItsRctCurrencyResponse>> currencies();

    public APICustomize<ItsRctCurrencyResponse> getCurrencyByCode(String code);

    public APICustomize<ItsRctCurrencyResponse> addCurrency(AddCurrencyRequest request);

    public APICustomize<String> deleteCurrency(String code);

    public APICustomize<ItsRctCurrencyResponse> updateCurrency(String code, AddCurrencyRequest request);

}
