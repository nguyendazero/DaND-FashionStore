package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCollectionResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.Collections;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Category;
import com.haibazo_bff_its_rct_webapi.model.Collection;
import com.haibazo_bff_its_rct_webapi.repository.CollectionRepository;
import com.haibazo_bff_its_rct_webapi.service.CollectionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final MinioServiceImpl minioService;
    private final String BUCKET_NAME = "collections";
    private final CollectionRepository collectionRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCollectionResponse>> collections() {
        List<Collection> collections = collectionRepository.findAll();
        List<ItsRctCollectionResponse> collectionResponses = collections.stream()
                .map(collection -> new ItsRctCollectionResponse(
                        collection.getId(),
                        collection.getCollectionType(),
                        collection.getName(),
                        collection.getDescription(),
                        collection.getImageUrl(),
                        collection.getCreatedAt(),
                        collection.getUpdatedAt()
                )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), collectionResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCollectionResponse> addCollection(AddCollectionRequest request) {
        try {
            Collection collection = new Collection();
            collection.setName(request.getName());
            collection.setCollectionType(Collections.valueOf(request.getCollectionType()));
            collection.setDescription(request.getDescription());

            MultipartFile imageFile = request.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = request.getCollectionType().replace(" ", "_");
                InputStream inputStream = imageFile.getInputStream();
                minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());
                collection.setImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName);
            } else {
                collection.setImageUrl(null);
            }

            // Save the collection to the repository
            Collection savedCollection = collectionRepository.save(collection);

            // Create the response object
            ItsRctCollectionResponse collectionResponse = new ItsRctCollectionResponse(
                    savedCollection.getId(),
                    savedCollection.getCollectionType(),
                    savedCollection.getName(),
                    savedCollection.getDescription(),
                    savedCollection.getImageUrl(),
                    savedCollection.getCreatedAt(),
                    savedCollection.getUpdatedAt()
            );

            return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), collectionResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding collection: " + e.getMessage(), e);
        }
    }

    private String extractImageName(String imageUrl) {
        String[] parts = imageUrl.split("/");
        return parts[parts.length - 1];
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> deleteCollection(Long id) {

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", id.toString()));

        String imageUrl = collection.getImageUrl();
        String imageName = null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageName = extractImageName(imageUrl);
        }

        collectionRepository.delete(collection);

        if (imageName != null && !imageName.isEmpty()) {
            minioService.deleteObject(BUCKET_NAME, imageName);
        }

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted collection with id = " + collection.getId());
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCollectionResponse> updateCollection(Long id, AddCollectionRequest request) {

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", id.toString()));

        try {
            collection.setCollectionType(Collections.valueOf(request.getCollectionType()));
            collection.setName(request.getName());
            collection.setDescription(request.getDescription());

            MultipartFile imageFile = request.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = request.getName().replace(" ", "_");
                InputStream inputStream = imageFile.getInputStream();

                // Optionally delete the old image from MinIO if it exists
                String existingImageUrl = collection.getImageUrl();
                String existingImageName = extractImageName(existingImageUrl);
                if (existingImageName != null && !existingImageName.isEmpty()) {
                    minioService.deleteObject(BUCKET_NAME, existingImageName);
                }

                // Upload the new image to MinIO
                minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());
                collection.setImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName);
            }

            // Save the updated category to the repository
            Collection updatedCollection = collectionRepository.save(collection);

            // Create the response object
            ItsRctCollectionResponse collectionResponse = new ItsRctCollectionResponse(
                    updatedCollection.getId(),
                    updatedCollection.getCollectionType(),
                    updatedCollection.getName(),
                    updatedCollection.getDescription(),
                    updatedCollection.getImageUrl(),
                    updatedCollection.getCreatedAt(),
                    updatedCollection.getUpdatedAt()
            );

            return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), collectionResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating collection: " + e.getMessage(), e);
        }
    }
}
