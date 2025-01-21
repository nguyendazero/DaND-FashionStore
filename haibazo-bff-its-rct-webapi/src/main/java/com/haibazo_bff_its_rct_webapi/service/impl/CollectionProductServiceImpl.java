package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionProductRequest;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.CollectionProductRepository;
import com.haibazo_bff_its_rct_webapi.repository.CollectionRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductRepository;
import com.haibazo_bff_its_rct_webapi.service.CollectionProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectionProductServiceImpl implements CollectionProductService {

    private final CollectionRepository collectionRepository;
    private final ProductRepository productRepository;
    private final CollectionProductRepository collectionProductRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<Void> add(AddCollectionProductRequest request) {
        // Lấy thông tin collection
        Collection collection = collectionRepository.findById(request.getCollectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", request.getCollectionId().toString()));

        // Lấy thông tin product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId().toString()));

        // Kiểm tra xem CollectionProduct đã tồn tại chưa
        if (collectionProductRepository.existsByProductIdAndCollectionId(product.getId(), collection.getId())) {
            throw new ResourceAlreadyExistsException("CollectionProduct", "collectionId va productId",
                    "Collection ID: " + collection.getId() + ", Product ID: " + product.getId());
        }

        CollectionProduct collectionProduct = new CollectionProduct();
        collectionProduct.setCollection(collection);
        collectionProduct.setProduct(product);

        collectionProductRepository.save(collectionProduct);

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), null);
    }
}
