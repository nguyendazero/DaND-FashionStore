package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddReviewRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctImageResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctReviewResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.ImageRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductRepository;
import com.haibazo_bff_its_rct_webapi.repository.ReviewRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserTempRepository;
import com.haibazo_bff_its_rct_webapi.service.MinioService;
import com.haibazo_bff_its_rct_webapi.service.RedisService;
import com.haibazo_bff_its_rct_webapi.service.ReviewService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RedisService redisService;
    private final UserTempRepository userTempRepository;
    private final ProductRepository productRepository;
    private final String BUCKET_NAME = "reviews";
    private final ImageRepository imageRepository;
    private final MinioService minioService;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctReviewResponse>> reviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        List<Review> reviews = reviewRepository.findAllByProduct(product);
        List<ItsRctReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    // Lấy danh sách hình ảnh liên quan đến review
                    List<Image> images = imageRepository.findByEntityIdAndEntityType(review.getId(), EntityType.REVIEW);

                    // Chuyển đổi hình ảnh thành ItsRctImageResponse
                    List<ItsRctImageResponse> imageResponses = images.stream()
                            .map(image -> new ItsRctImageResponse(
                                    image.getId(),
                                    image.getImageUrl(),
                                    image.getEntityType(),
                                    image.getEntityId()
                            )).toList();

                    return new ItsRctReviewResponse(
                            review.getId(),
                            review.getContent(),
                            review.getStars(),
                            imageResponses,
                            review.getUser(),
                            review.getUserTemp(),
                            review.getCreatedAt(),
                            review.getUpdatedAt()
                    );
                }).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), reviewResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctReviewResponse> review(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id.toString()));

        // Lấy danh sách hình ảnh liên quan đến review
        List<Image> images = imageRepository.findByEntityIdAndEntityType(review.getId(), EntityType.REVIEW);

        // Chuyển đổi hình ảnh thành ItsRctImageResponse
        List<ItsRctImageResponse> imageResponses = images.stream()
                .map(image -> new ItsRctImageResponse(
                        image.getId(),
                        image.getImageUrl(),
                        image.getEntityType(),
                        image.getEntityId()
                )).toList();

        // Tạo phản hồi
        ItsRctReviewResponse response = new ItsRctReviewResponse(
                review.getId(),
                review.getContent(),
                review.getStars(),
                imageResponses,
                review.getUser(),
                review.getUserTemp(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctReviewResponse> add(Long productId, AddReviewRequest request) {
        // Tìm sản phẩm theo productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        // Tạo UserTemp và lưu vào cơ sở dữ liệu
        UserTemp userTemp = new UserTemp();
        userTemp.setFullName(request.getFullName());
        userTemp.setEmail(request.getEmail());
        userTemp.setAvatar(null);
        userTempRepository.save(userTemp);

        // Tạo Review và thiết lập các thuộc tính
        Review review = new Review();
        review.setStars(request.getStars());
        review.setContent(request.getContent());
        review.setProduct(product);
        review.setUser(null);
        review.setUserTemp(userTemp);

        // Lưu review vào cơ sở dữ liệu trước
        Review savedReview = reviewRepository.save(review);

        List<ItsRctImageResponse> imageResponses = new ArrayList<>();
        List<MultipartFile> imageFiles = request.getImages();

        // Xử lý danh sách ảnh
        for (MultipartFile image : imageFiles) {
            if (image != null && !image.isEmpty()) {
                try {
                    // Tạo tên ảnh duy nhất
                    String imageName = request.getEmail().replace(" ", "_") + "_image_" + UUID.randomUUID().toString();
                    InputStream inputStream = image.getInputStream();

                    // Lưu ảnh vào MinIO
                    minioService.putObject(BUCKET_NAME, imageName, inputStream, image.getContentType());

                    // Tạo URL cho ảnh
                    String imageUrl = "/api/bff/its-rct/v1/ecommerce/public/image/" + BUCKET_NAME + "/" + imageName;

                    // Tạo và lưu thông tin ảnh vào cơ sở dữ liệu
                    Image imageEntity = new Image();
                    imageEntity.setImageUrl(imageUrl);
                    imageEntity.setEntityType(EntityType.REVIEW);
                    imageEntity.setEntityId(savedReview.getId()); // Đặt entityId là ID của Review

                    // Lưu ảnh vào cơ sở dữ liệu
                    Image savedImage = imageRepository.save(imageEntity);

                    // Lưu thông tin ảnh vào danh sách response
                    ItsRctImageResponse imageResponse = new ItsRctImageResponse();
                    imageResponse.setId(savedImage.getId());
                    imageResponse.setImageUrl(savedImage.getImageUrl());
                    imageResponse.setEntityType(savedImage.getEntityType());
                    imageResponse.setEntityId(savedImage.getEntityId());
                    imageResponses.add(imageResponse);
                } catch (IOException e) {
                    // Xử lý lỗi nếu có
                    throw new RuntimeException("Error while processing image: " + e.getMessage(), e);
                }
            }
        }

        // Xóa cache trong Redis để cập nhật dữ liệu mới
        redisService.deleteKeysStartingWith("products");

        // Tạo phản hồi
        ItsRctReviewResponse response = new ItsRctReviewResponse(
                savedReview.getId(),
                savedReview.getContent(),
                savedReview.getStars(),
                imageResponses,
                savedReview.getUser(),
                savedReview.getUserTemp(),
                savedReview.getCreatedAt(),
                savedReview.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }


    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id.toString()));

        List<Image> images = imageRepository.findByEntityIdAndEntityType(review.getId(), EntityType.REVIEW);

        // Xóa từng hình ảnh khỏi MinIO và cơ sở dữ liệu
        for (Image image : images) {
            String imageName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
            minioService.deleteObject(BUCKET_NAME, imageName);
            imageRepository.delete(image);
        }
        // Xóa review khỏi cơ sở dữ liệu
        reviewRepository.delete(review);

        // Xóa cache trong Redis để cập nhật dữ liệu mới
        redisService.deleteKeysStartingWith("products");

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted review with id = " + review.getId());
    }
}
