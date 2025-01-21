package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCategoryRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Category;
import com.haibazo_bff_its_rct_webapi.repository.CategoryRepository;
import com.haibazo_bff_its_rct_webapi.service.CategoryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MinioServiceImpl minioService;
    private final String BUCKET_NAME = "categories";

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCategoryResponse>> categories() {
        List<Category> categories = categoryRepository.findAll();
        List<ItsRctCategoryResponse> categoryResponses = categories.stream()
                .map(category -> new ItsRctCategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getImageUrl(),
                        category.getCreatedAt(),
                        category.getUpdatedAt()
                )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), categoryResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCategoryResponse> category(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));

        ItsRctCategoryResponse categoryResponse = new ItsRctCategoryResponse(
                category.getId(),
                category.getName(),
                category.getImageUrl(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), categoryResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCategoryResponse> addCategory(AddCategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Category", "name", request.getName());
        }

        try {
            Category category = new Category();
            category.setName(request.getName());

            MultipartFile imageFile = request.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = request.getName().replace(" ", "_");
                InputStream inputStream = imageFile.getInputStream();
                minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());
                category.setImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName);
            } else {
                category.setImageUrl(null);
            }

            // Save the category to the repository
            Category savedCategory = categoryRepository.save(category);

            // Create the response object
            ItsRctCategoryResponse categoryResponse = new ItsRctCategoryResponse(
                    savedCategory.getId(),
                    savedCategory.getName(),
                    savedCategory.getImageUrl(),
                    savedCategory.getCreatedAt(),
                    savedCategory.getUpdatedAt()
            );

            return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), categoryResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding category: " + e.getMessage(), e);
        }
    }

    private String extractImageName(String imageUrl) {
        String[] parts = imageUrl.split("/");
        return parts[parts.length - 1];
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));

        String imageUrl = category.getImageUrl();
        String imageName = null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageName = extractImageName(imageUrl);
        }

        categoryRepository.delete(category);

        if (imageName != null && !imageName.isEmpty()) {
            minioService.deleteObject(BUCKET_NAME, imageName);
        }

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted category with id = " + category.getId());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCategoryResponse> updateCategory(Long id, AddCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));
        try {
            category.setName(request.getName());

            MultipartFile imageFile = request.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = request.getName().replace(" ", "_");
                InputStream inputStream = imageFile.getInputStream();

                // Optionally delete the old image from MinIO if it exists
                String existingImageUrl = category.getImageUrl();
                String existingImageName = extractImageName(existingImageUrl);
                if (existingImageName != null && !existingImageName.isEmpty()) {
                    minioService.deleteObject(BUCKET_NAME, existingImageName);
                }

                // Upload the new image to MinIO
                minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());
                category.setImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName);
            }

            // Save the updated category to the repository
            Category updatedCategory = categoryRepository.save(category);

            // Create the response object
            ItsRctCategoryResponse categoryResponse = new ItsRctCategoryResponse(
                    updatedCategory.getId(),
                    updatedCategory.getName(),
                    updatedCategory.getImageUrl(),
                    updatedCategory.getCreatedAt(),
                    updatedCategory.getUpdatedAt()
            );

            return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), categoryResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating category: " + e.getMessage(), e);
        }
    }
}
