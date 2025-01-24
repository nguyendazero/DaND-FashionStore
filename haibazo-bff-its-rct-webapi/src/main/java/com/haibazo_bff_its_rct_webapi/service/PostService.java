package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostResponse;

import java.util.List;

public interface PostService {

    public APICustomize<ItsRctPostResponse> add(AddPostRequest request);

    public APICustomize<ItsRctPostResponse> post(Long id);

    public APICustomize<List<ItsRctPostResponse>> posts();

    public APICustomize<String> delete(Long id);
    
}
