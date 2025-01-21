package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Category;
import com.haibazo_bff_its_rct_webapi.model.Post;
import com.haibazo_bff_its_rct_webapi.model.Product;
import com.haibazo_bff_its_rct_webapi.repository.CategoryRepository;
import com.haibazo_bff_its_rct_webapi.repository.PostRepository;
import com.haibazo_bff_its_rct_webapi.service.CategoryService;
import com.haibazo_bff_its_rct_webapi.service.PostService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostResponse> add(AddPostRequest request) {

        // Lấy thông tin category
        APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(request.getCategoryId());
        Category category = categoryRepository.findById(categoryResponse.getResult().getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId().toString()));

        Post post = new Post();
        post.setName(request.getName());
        post.setContent(request.getContent());
        post.setCategory(category);

        Post savedPost = postRepository.save(post);

        ItsRctPostResponse response = new ItsRctPostResponse(
                savedPost.getId(),
                savedPost.getName(),
                savedPost.getContent(),
                categoryResponse.getResult(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostResponse> post(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        ItsRctPostResponse response = new ItsRctPostResponse(
                post.getId(),
                post.getName(),
                post.getContent(),
                categoryService.category(post.getCategory().getId()).getResult(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctPostResponse>> posts() {

        List<Post> posts = postRepository.findAll();

        List<ItsRctPostResponse> postResponses = posts.stream()
                .map(post -> new ItsRctPostResponse(
                        post.getId(),
                        post.getName(),
                        post.getContent(),
                        categoryService.category(post.getCategory().getId()).getResult(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), postResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        postRepository.delete(post);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted post with id = " + post.getId());
    }
}
