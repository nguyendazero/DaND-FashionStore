package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostCommentRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostCommentResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Post;
import com.haibazo_bff_its_rct_webapi.model.PostComment;
import com.haibazo_bff_its_rct_webapi.model.Product;
import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import com.haibazo_bff_its_rct_webapi.repository.PostCommentRepository;
import com.haibazo_bff_its_rct_webapi.repository.PostRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserTempRepository;
import com.haibazo_bff_its_rct_webapi.service.PostCommentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserTempRepository userTempRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostCommentResponse> add(Long postId, AddPostCommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));

        // Tạo UserTemp và lưu vào cơ sở dữ liệu
        UserTemp userTemp = new UserTemp();
        userTemp.setFullName(request.getFullName());
        userTemp.setEmail(request.getEmail());
        userTemp.setAvatar(null);
        userTempRepository.save(userTemp);

        PostComment postComment = new PostComment();
        postComment.setContent(request.getContent());
        postComment.setPost(post);
        postComment.setUser(null);
        postComment.setUserTemp(userTemp);

        PostComment savedPostComment = postCommentRepository.save(postComment);

        ItsRctPostCommentResponse response = new ItsRctPostCommentResponse(
                savedPostComment.getId(),
                savedPostComment.getContent(),
                savedPostComment.getUser(),
                savedPostComment.getUserTemp(),
                savedPostComment.getPost().getId(),
                savedPostComment.getCreatedAt(),
                savedPostComment.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostCommentResponse> postComment(Long id) {

        PostComment postComment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PostComment", "id", id.toString()));

        ItsRctPostCommentResponse response = new ItsRctPostCommentResponse(
                postComment.getId(),
                postComment.getContent(),
                postComment.getUser(),
                postComment.getUserTemp(),
                postComment.getPost().getId(),
                postComment.getCreatedAt(),
                postComment.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctPostCommentResponse>> getByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));

        List<PostComment> postComments = postCommentRepository.findAllByPost(post);
        List<ItsRctPostCommentResponse> response = postComments.stream()
                .map(postComment -> new ItsRctPostCommentResponse(
                        postComment.getId(),
                        postComment.getContent(),
                        postComment.getUser(),
                        postComment.getUserTemp(),
                        postComment.getPost().getId(),
                        postComment.getCreatedAt(),
                        postComment.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        PostComment postComment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PostComment", "id", id.toString()));

        postCommentRepository.delete(postComment);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted post comment with id = " + postComment.getId());
    }
}
