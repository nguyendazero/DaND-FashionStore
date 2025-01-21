package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddReviewRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctReviewResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReviewService {

    public APICustomize<List<ItsRctReviewResponse>> reviews(Long productId);

    public APICustomize<ItsRctReviewResponse> review(Long reviewId);

    public APICustomize<ItsRctReviewResponse> add(Long productId, AddReviewRequest request);

    public APICustomize<String> delete(Long id);

}
