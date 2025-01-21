package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionProductRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostTagRequest;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.PostRepository;
import com.haibazo_bff_its_rct_webapi.repository.PostTagRepository;
import com.haibazo_bff_its_rct_webapi.repository.TagRepository;
import com.haibazo_bff_its_rct_webapi.service.PostTagService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostTagServiceImpl implements PostTagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;


    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<Void> add(AddPostTagRequest request) {
        // Lấy thông tin tag
        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", request.getTagId().toString()));

        // Lấy thông tin post
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", request.getPostId().toString()));

        // Kiểm tra xem PostTag đã tồn tại chưa
        if (postTagRepository.existsByPostIdAndTagId(post.getId(), tag.getId())) {
            throw new ResourceAlreadyExistsException("PostTag", "postId va tagId",
                    "Post ID: " + post.getId() + ", Tag ID: " + tag.getId());
        }

        PostTag postTag = new PostTag();
        postTag.setTag(tag);
        postTag.setPost(post);

        postTagRepository.save(postTag);

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), null);
    }
}
