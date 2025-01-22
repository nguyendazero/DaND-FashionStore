package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddQuestionRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctImageResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctQuestionResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.ImageRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductRepository;
import com.haibazo_bff_its_rct_webapi.repository.QuestionRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserTempRepository;
import com.haibazo_bff_its_rct_webapi.service.MinioService;
import com.haibazo_bff_its_rct_webapi.service.QuestionService;
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
public class QuestionServiceImpl implements QuestionService {

    private final ProductRepository productRepository;
    private final QuestionRepository questionRepository;
    private final UserTempRepository userTempRepository;
    private final String BUCKET_NAME = "questions";
    private final ImageRepository imageRepository;
    private final MinioService minioService;

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctQuestionResponse> add(Long productId, AddQuestionRequest request) {
        // Tìm sản phẩm theo productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        // Tạo UserTemp và lưu vào cơ sở dữ liệu
        UserTemp userTemp = new UserTemp();
        userTemp.setFullName(request.getFullName());
        userTemp.setEmail(request.getEmail());
        userTemp.setAvatar(null);
        userTempRepository.save(userTemp);

        // Tạo Question và thiết lập các thuộc tính
        Question question = new Question();
        question.setContent(request.getContent());
        question.setProduct(product);
        question.setUser(null);
        question.setUserTemp(userTemp);

        // Lưu question vào cơ sở dữ liệu trước
        Question savedQuestion = questionRepository.save(question);

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
                    imageEntity.setEntityType(EntityType.QUESTION);
                    imageEntity.setEntityId(savedQuestion.getId()); // Đặt entityId là ID của Question

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
                    throw new RuntimeException("Error while processing image: " + e.getMessage(), e);
                }
            }
        }

        // Tạo phản hồi
        ItsRctQuestionResponse response = new ItsRctQuestionResponse(
                savedQuestion.getId(),
                savedQuestion.getUser(),
                savedQuestion.getUserTemp(),
                savedQuestion.getProduct().getId(),
                savedQuestion.getContent(),
                imageResponses,
                savedQuestion.getCreatedAt(),
                savedQuestion.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctQuestionResponse>> questionsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        List<Question> questions = questionRepository.findAllByProduct(product);
        List<ItsRctQuestionResponse> questionResponses = questions.stream()
                .map(question -> {
                    // Lấy danh sách hình ảnh liên quan đến câu hỏi
                    List<Image> images = imageRepository.findByEntityIdAndEntityType(question.getId(), EntityType.QUESTION);

                    // Chuyển đổi hình ảnh thành ItsRctImageResponse
                    List<ItsRctImageResponse> imageResponses = images.stream()
                            .map(image -> new ItsRctImageResponse(
                                    image.getId(),
                                    image.getImageUrl(),
                                    image.getEntityType(),
                                    image.getEntityId()
                            )).toList();

                    return new ItsRctQuestionResponse(
                            question.getId(),
                            question.getUser(),
                            question.getUserTemp(),
                            question.getProduct().getId(),
                            question.getContent(),
                            imageResponses,
                            question.getCreatedAt(),
                            question.getUpdatedAt()
                    );
                }).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), questionResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctQuestionResponse> getById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id.toString()));

        // Lấy danh sách hình ảnh liên quan đến câu hỏi
        List<Image> images = imageRepository.findByEntityIdAndEntityType(question.getId(), EntityType.QUESTION);

        // Chuyển đổi hình ảnh thành ItsRctImageResponse
        List<ItsRctImageResponse> imageResponses = images.stream()
                .map(image -> new ItsRctImageResponse(
                        image.getId(),
                        image.getImageUrl(),
                        image.getEntityType(),
                        image.getEntityId()
                )).toList();

        // Tạo phản hồi
        ItsRctQuestionResponse response = new ItsRctQuestionResponse(
                question.getId(),
                question.getUser(),
                question.getUserTemp(),
                question.getProduct().getId(),
                question.getContent(),
                imageResponses,
                question.getCreatedAt(),
                question.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id.toString()));

        List<Image> images = imageRepository.findByEntityIdAndEntityType(question.getId(), EntityType.QUESTION);

        // Xóa từng hình ảnh khỏi MinIO và cơ sở dữ liệu
        for (Image image : images) {
            String imageName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
            minioService.deleteObject(BUCKET_NAME, imageName);
            imageRepository.delete(image);
        }
        // Xóa câu hỏi khỏi cơ sở dữ liệu
        questionRepository.delete(question);

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted question with id = " + question.getId());
    }
}
