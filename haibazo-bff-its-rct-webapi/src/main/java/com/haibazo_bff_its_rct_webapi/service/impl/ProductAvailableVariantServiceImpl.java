package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductAvailableVariantRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDiscountResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductAvailableVariantResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductVariantResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Discount;
import com.haibazo_bff_its_rct_webapi.model.Product;
import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import com.haibazo_bff_its_rct_webapi.model.ProductVariant;
import com.haibazo_bff_its_rct_webapi.repository.DiscountRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductAvailableVariantRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductVariantRepository;
import com.haibazo_bff_its_rct_webapi.service.DiscountService;
import com.haibazo_bff_its_rct_webapi.service.ProductAvailableVariantService;
import com.haibazo_bff_its_rct_webapi.service.RedisService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAvailableVariantServiceImpl implements ProductAvailableVariantService {

    private final ProductAvailableVariantRepository productAvailableVariantRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final DiscountService discountService;
    private final MinioServiceImpl minioService;
    private final String BUCKET_NAME = "product-available-variants";
    private final ProductVariantRepository productVariantRepository;
    private final RedisService redisService;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctProductAvailableVariantResponse>> productAvailableVariants(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        // Lấy tất cả các biến thể sản phẩm cho sản phẩm cụ thể
        List<ProductAvailableVariant> productAvailableVariants = productAvailableVariantRepository.findByProductId(productId);

        // Chuyển đổi danh sách biến thể sản phẩm thành danh sách phản hồi
        List<ItsRctProductAvailableVariantResponse> productAvailableVariantResponses = productAvailableVariants.stream()
                .map(productAvailableVariant -> {
                    // Lấy thông tin discount cho biến thể sản phẩm
                    APICustomize<ItsRctDiscountResponse> discountResponse = null;
                    if (productAvailableVariant.getDiscount() != null) {
                        discountResponse = discountService.discount(productAvailableVariant.getDiscount().getId());
                    }

                    // Lấy danh sách biến thể sản phẩm cho productAvailableVariant
                    List<ProductVariant> productVariants = productVariantRepository.findByProductAvailableVariant(productAvailableVariant);
                    List<ItsRctProductVariantResponse> productVariantResponses = productVariants.stream()
                            .map(productVariant -> new ItsRctProductVariantResponse(
                                    productVariant.getVariantGroupKey().getName(),
                                    productVariant.getValue()
                            )).toList();

                    return new ItsRctProductAvailableVariantResponse(
                            productAvailableVariant.getId(),
                            productAvailableVariant.getHighLightedImageUrl(),
                            productAvailableVariant.getPrice(),
                            productAvailableVariant.getStock(),
                            productAvailableVariant.getProduct().getId(),
                            discountResponse != null ? discountResponse.getResult() : null,
                            productVariantResponses
                    );
                }).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productAvailableVariantResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctProductAvailableVariantResponse> productAvailableVariant(Long id) {
        return null;
    }

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctProductAvailableVariantResponse> add(Long productId, AddProductAvailableVariantRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        APICustomize<ItsRctDiscountResponse> discountResponse = null;
        Discount discount = null;
        if (request.getDiscountId() != null) {
            discountResponse = discountService.discount(request.getDiscountId());
            discount = discountRepository.findById(discountResponse.getResult().getDiscountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", request.getDiscountId().toString()));
        }

        MultipartFile imageFile = request.getHighlightedImage();

        // Tạo một ProductAvailableVariant (nhưng chưa lưu vào DB) để có ID
        ProductAvailableVariant productAvailableVariant = new ProductAvailableVariant();
        productAvailableVariant.setProduct(product);
        productAvailableVariant.setPrice(request.getPrice());
        productAvailableVariant.setStock(request.getStock());
        productAvailableVariant.setDiscount(discount);
        productAvailableVariant.setHighLightedImageUrl("temp");
        ProductAvailableVariant savedTempProductAvailableVariant = productAvailableVariantRepository.save(productAvailableVariant);

        // Tạo tên hình ảnh duy nhất và lưu hình ảnh vào MinIO
        String imageName = product.getName().replace(" ", "_")+ "_" + savedTempProductAvailableVariant.getId();
        InputStream inputStream = imageFile.getInputStream();
        minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());
        // Cập nhật lại URL hình ảnh sau khi lưu
        productAvailableVariant.setHighLightedImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName);

        // Lưu biến thể vào DB
        ProductAvailableVariant savedProductAvailableVariant = productAvailableVariantRepository.save(productAvailableVariant);

        //Xóa cache in Redis to update new data
        redisService.deleteKeysStartingWith("products");

        ItsRctProductAvailableVariantResponse response = new ItsRctProductAvailableVariantResponse(
                savedProductAvailableVariant.getId(),
                savedProductAvailableVariant.getHighLightedImageUrl(),
                savedProductAvailableVariant.getPrice(),
                savedProductAvailableVariant.getStock(),
                savedProductAvailableVariant.getProduct().getId(),
                discountResponse != null ? discountResponse.getResult() : null,
                new ArrayList<>()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    public APICustomize<ItsRctProductAvailableVariantResponse> update(Long id, AddProductAvailableVariantRequest request) {
        return null;
    }

    @Override
    public APICustomize<String> delete(Long id) {
        return null;
    }
}
