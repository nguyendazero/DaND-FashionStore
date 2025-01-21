package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductVariantRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductVariantResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import com.haibazo_bff_its_rct_webapi.model.ProductVariant;
import com.haibazo_bff_its_rct_webapi.model.VariantGroup;
import com.haibazo_bff_its_rct_webapi.repository.ProductAvailableVariantRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductVariantRepository;
import com.haibazo_bff_its_rct_webapi.repository.VariantGroupRepository;
import com.haibazo_bff_its_rct_webapi.service.ProductVariantService;
import com.haibazo_bff_its_rct_webapi.service.RedisService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductAvailableVariantRepository productAvailableVariantRepository;
    private final VariantGroupRepository variantGroupRepository;
    private final RedisService redisService;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctProductVariantResponse>> productVariants(Long productAvailableVariantId) {
        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository
                .findById(productAvailableVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", productAvailableVariantId.toString()));

        List<ProductVariant> productVariants = productVariantRepository.findByProductAvailableVariant(productAvailableVariant);
        List<ItsRctProductVariantResponse> productVariantResponses = productVariants.stream()
                .map(productVariant -> new ItsRctProductVariantResponse(
                        productVariant.getVariantGroupKey().getName(),
                        productVariant.getValue()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productVariantResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctProductVariantResponse> addProductVariant(Long productAvailableVariantId, AddProductVariantRequest request) {
        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository
                .findById(productAvailableVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", productAvailableVariantId.toString()));

        VariantGroup variantGroup = variantGroupRepository
                .findByVariantKey(request.getVariantGroupKey())
                .orElseThrow(() -> new ResourceNotFoundException("VariantGroup", "key", request.getVariantGroupKey()));

        ProductVariant productVariant = new ProductVariant();
        productVariant.setVariantGroupKey(variantGroup);
        productVariant.setValue(request.getValue());
        productVariant.setProductAvailableVariant(productAvailableVariant);

        ProductVariant savedProductVariant = productVariantRepository.save(productVariant);

        //Xóa cache in Redis to update new data
        redisService.deleteKeysStartingWith("products");

        ItsRctProductVariantResponse productVariantResponse = new ItsRctProductVariantResponse(
                savedProductVariant.getVariantGroupKey().getName(),
                savedProductVariant.getValue()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productVariantResponse);
    }
}
