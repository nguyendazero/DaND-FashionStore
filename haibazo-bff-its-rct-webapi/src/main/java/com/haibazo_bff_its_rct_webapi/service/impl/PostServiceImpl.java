package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctTagResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Category;
import com.haibazo_bff_its_rct_webapi.model.Post;
import com.haibazo_bff_its_rct_webapi.model.Tag;
import com.haibazo_bff_its_rct_webapi.repository.CategoryRepository;
import com.haibazo_bff_its_rct_webapi.repository.PostRepository;
import com.haibazo_bff_its_rct_webapi.service.CategoryService;
import com.haibazo_bff_its_rct_webapi.service.MinioService;
import com.haibazo_bff_its_rct_webapi.service.PostService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final String BUCKET_NAME = "posts";
    private final MinioService minioService;

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostResponse> add(AddPostRequest request) {

        // Lấy thông tin category
        APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(request.getCategoryId());
        Category category = categoryRepository.findById(categoryResponse.getResult().getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId().toString()));

        // Tạo bài viết
        Post post = new Post();
        post.setName(request.getName());
        post.setContent(request.getContent());
        post.setCategory(category);

        // Xử lý hình ảnh
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                // Tạo tên ảnh duy nhất
                String imageName = "post_image_" + UUID.randomUUID();
                InputStream inputStream = request.getImage().getInputStream();

                // Lưu ảnh vào MinIO
                minioService.putObject(BUCKET_NAME, imageName, inputStream, request.getImage().getContentType());

                // Tạo URL cho ảnh
                String imageUrl = "/api/bff/its-rct/v1/ecommerce/public/image/" + BUCKET_NAME + "/" + imageName;

                // Cập nhật imageUrl vào post
                post.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Error while processing image: " + e.getMessage(), e);
            }
        }

        // Lưu bài viết vào cơ sở dữ liệu
        Post savedPost = postRepository.save(post);

        // Tạo phản hồi
        ItsRctPostResponse response = new ItsRctPostResponse(
                savedPost.getId(),
                savedPost.getName(),
                savedPost.getContent(),
                savedPost.getImageUrl(),
                categoryResponse.getResult(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostResponse> post(Long id) {
        // Tìm bài viết theo id
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        // Lấy thông tin danh mục
        APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(post.getCategory().getId());

        // Tạo phản hồi
        ItsRctPostResponse response = new ItsRctPostResponse(
                post.getId(),
                post.getName(),
                post.getContent(),
                post.getImageUrl(),
                categoryResponse.getResult(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctPostResponse>> posts() {
        // Lấy tất cả bài viết
        List<Post> posts = postRepository.findAll();

        // Lấy danh sách phản hồi cho từng bài viết
        List<ItsRctPostResponse> postResponses = posts.stream()
                .map(post -> {
                    // Lấy thông tin danh mục
                    APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(post.getCategory().getId());

                    // Tạo phản hồi cho mỗi bài viết
                    return new ItsRctPostResponse(
                            post.getId(),
                            post.getName(),
                            post.getContent(),
                            post.getImageUrl(),
                            categoryResponse.getResult(),
                            post.getCreatedAt(),
                            post.getUpdatedAt()
                    );
                })
                .toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), postResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {
        // Tìm bài viết theo id
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));

        // Xóa hình ảnh từ MinIO nếu có
        if (post.getImageUrl() != null) {
            String imageName = post.getImageUrl().substring(post.getImageUrl().lastIndexOf("/") + 1);
            minioService.deleteObject(BUCKET_NAME, imageName);
        }

        // Xóa bài viết khỏi cơ sở dữ liệu
        postRepository.delete(post);

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted post with id = " + post.getId());
    }

    @Override
    public APICustomize<List<ItsRctTagResponse>> getTagsByPostId(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id.toString()));
        
        List<ItsRctTagResponse> tagsResponse = post.getPostTags().stream()
                .map(postTag -> {
                    Tag tag = postTag.getTag();
                    return new ItsRctTagResponse(tag.getId(), tag.getName(), tag.getCreatedAt(), tag.getUpdatedAt());
                }) .toList();
        
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), tagsResponse);
    }
}
