package com.haibazo_bff_its_rct_webapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haibazo.bff.common.extension.exception.NotFoundException;
import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddLanguageRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctLanguageResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Language;
import com.haibazo_bff_its_rct_webapi.repository.LanguageRepository;
import com.haibazo_bff_its_rct_webapi.service.LanguageService;
import com.haibazo_bff_its_rct_webapi.service.RedisService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final MinioServiceImpl minioService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final String BUCKET_NAME = "languages";

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctLanguageResponse>> languages() {

        String redisKey = "languages";
        // Lấy dữ liệu từ Redis
        List<String> cachedData = redisService.getList(redisKey);
        if (cachedData != null && !cachedData.isEmpty()) {
            List<ItsRctLanguageResponse> languageResponses = cachedData.stream()
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, ItsRctLanguageResponse.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());

            System.out.println("Lấy từ Redis");
            return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), languageResponses);
        }
        // Nếu không có dữ liệu trong Redis, lấy từ cơ sở dữ liệu
        List<Language> languages = languageRepository.findAll();
        List<ItsRctLanguageResponse> languageResponses = languages.stream()
                .map(language -> new ItsRctLanguageResponse(
                        language.getCode(),
                        language.getName(),
                        language.getImageUrl(),
                        language.getCreatedAt(),
                        language.getUpdatedAt()
                )).collect(Collectors.toList());

        // Lấy xong Lưu vào Redis
        for (ItsRctLanguageResponse item : languageResponses) {
            String jsonItem = objectMapper.writeValueAsString(item);
            redisService.addToList(redisKey, jsonItem);
        }

        System.out.println("Lấy từ database");
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), languageResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctLanguageResponse> addLanguage(AddLanguageRequest request) {
        try {
            // Lưu hình ảnh vào MinIO
            MultipartFile imageFile = request.getImage();
            String imageName = request.getName().replace(" ", "_");
            InputStream inputStream = imageFile.getInputStream();
            minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());

            // Tạo đối tượng Language
            Language language = new Language();
            language.setCode(request.getCode());
            language.setName(request.getName());
            language.setImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/"+ imageName);
            Language savedLanguage = languageRepository.save(language);

            //Xóa cache in Redis to update new data
            redisService.deleteKey("languages");

            ItsRctLanguageResponse languageResponse = new ItsRctLanguageResponse(
                    savedLanguage.getCode(),
                    savedLanguage.getName(),
                    savedLanguage.getImageUrl(),
                    savedLanguage.getCreatedAt(),
                    savedLanguage.getUpdatedAt()
            );
            return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), languageResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding language: " + e.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctLanguageResponse> getLanguageByCode(String code) {

        Language language = languageRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "code", code));

        ItsRctLanguageResponse languageResponse = new ItsRctLanguageResponse(
                language.getCode(),
                language.getName(),
                language.getImageUrl(),
                language.getCreatedAt(),
                language.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), languageResponse);
    }

    private String extractImageName(String imageUrl) {
        String[] parts = imageUrl.split("/");
        return parts[parts.length - 1];
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> deleteByCode(String code) {
        Language language = languageRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("NOTFOUND4041E"));

        String imageUrl = language.getImageUrl();
        String imageName = extractImageName(imageUrl);

        languageRepository.delete(language);
        if (imageName != null && !imageName.isEmpty()) {
            minioService.deleteObject(BUCKET_NAME, imageName);
        }

        //Xóa cache in Redis to update new data
        redisService.deleteKey("languages");

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted languages with code = " + language.getCode());
    }
}
