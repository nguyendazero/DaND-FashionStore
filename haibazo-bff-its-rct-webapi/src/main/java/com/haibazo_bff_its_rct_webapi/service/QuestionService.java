package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddQuestionRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctQuestionResponse;
import com.haibazo_bff_its_rct_webapi.model.Product;

import java.util.List;

public interface QuestionService {

    public APICustomize<ItsRctQuestionResponse> add(Long productId, AddQuestionRequest request,  String authorizationHeader);

    public APICustomize<List<ItsRctQuestionResponse>> questionsByProduct(Long productId);

    public APICustomize<ItsRctQuestionResponse> getById(Long id);

    public APICustomize<String> delete(Long id, String authorizationHeader);

}
