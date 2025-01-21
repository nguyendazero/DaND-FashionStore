package com.haibazo_bff_its_rct_webapi.service.impl;
import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctImageResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.model.Image;
import com.haibazo_bff_its_rct_webapi.repository.ImageRepository;
import com.haibazo_bff_its_rct_webapi.service.ImageService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctImageResponse>> getImages(Long entityId, EntityType entityType) {
        List<Image> images = imageRepository.findByEntityIdAndEntityType(entityId, entityType);
        List<ItsRctImageResponse> imageResponses = images.stream()
                .map(image -> new ItsRctImageResponse(
                        image.getId(),
                        image.getImageUrl(),
                        image.getEntityType(),
                        image.getEntityId()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), imageResponses);
    }
}
