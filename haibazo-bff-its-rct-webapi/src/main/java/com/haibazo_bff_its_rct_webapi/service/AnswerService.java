package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAnswerRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAnswerResponse;

import java.util.List;

public interface AnswerService {

    public APICustomize<ItsRctAnswerResponse> add(Long questionId, AddAnswerRequest request);

    public APICustomize<ItsRctAnswerResponse> getById(Long id);

    public APICustomize<List<ItsRctAnswerResponse>> getByQuestionId(Long questionId);

    public APICustomize<String> delete(Long id);

}
