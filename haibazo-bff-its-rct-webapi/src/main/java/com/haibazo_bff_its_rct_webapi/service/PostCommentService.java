package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostCommentRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostCommentResponse;

import java.util.List;

public interface PostCommentService {

    public APICustomize<ItsRctPostCommentResponse> add(Long postId, AddPostCommentRequest request);

    public APICustomize<ItsRctPostCommentResponse> postComment(Long id);

    public APICustomize<List<ItsRctPostCommentResponse>> getByPostId(Long postId);

    public APICustomize<String> delete(Long id);

}
