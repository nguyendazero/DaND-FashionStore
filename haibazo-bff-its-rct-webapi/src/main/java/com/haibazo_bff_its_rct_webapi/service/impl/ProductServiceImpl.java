package com.haibazo_bff_its_rct_webapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haibazo_bff_its_rct_webapi.enums.Collections;
import com.haibazo_bff_its_rct_webapi.exception.ListProductEmptyException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.specification.ProductSpecification;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.haibazo.bff.common.extension.exception.BadRequestException;
import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.*;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.exception.InvalidPageOrSizeException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.*;
import com.haibazo_bff_its_rct_webapi.service.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final DiscountRepository discountRepository;
    private final DiscountService discountService;
    private final MinioServiceImpl minioService;
    private final String BUCKET_NAME = "products";
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final ReviewRepository reviewRepository;
    private final ProductAvailableVariantService productAvailableVariantService;
    private final StyleRepository styleRepository;
    private final StyleService styleService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctProductResponse>> products(String size, String color, BigDecimal minPrice, BigDecimal maxPrice, String style, String category, Collections collection, String name, String sortBy, String sortOrder, int pageIndex, int pageSize) {

        // Tạo khóa Redis dựa trên các tham số lọc
        String redisKey = String.format("products:size=%s;color=%s;minPrice=%s;maxPrice=%s;style=%s;category=%s;collection=%s;name=%s;sortBy=%s;sortOrder=%s;pageIndex=%d;pageSize=%d",
                size, color, minPrice, maxPrice, style, category, collection, name, sortBy, sortOrder, pageIndex, pageSize);

        // Lấy dữ liệu từ Redis
        List<String> cachedData = redisService.getList(redisKey);
        if (cachedData != null && !cachedData.isEmpty()) {
            List<ItsRctProductResponse> productResponses = cachedData.stream()
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, ItsRctProductResponse.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();

            System.out.println("Lấy từ Redis");
            return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productResponses);
        }

        // Nếu không có dữ liệu trong Redis, lấy từ cơ sở dữ liệu

        if (pageIndex < 0 || pageSize <= 0) {
            throw new InvalidPageOrSizeException();
        }

        // Tạo specification với các tiêu chí tìm kiếm
        Specification<Product> spec = Specification.where(ProductSpecification.hasSize(size))
                .and(ProductSpecification.hasColor(color))
                .and(ProductSpecification.hasMinPrice(minPrice))
                .and(ProductSpecification.hasMaxPrice(maxPrice))
                .and(ProductSpecification.hasCategory(category))
                .and(ProductSpecification.hasStyle(style))
                .and(ProductSpecification.hasCollectionType(collection))
                .and(ProductSpecification.hasName(name))
                .and(ProductSpecification.sortBy(sortBy, sortOrder));

        // Sử dụng Pageable từ Spring Data
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        // Tìm danh sách sản phẩm với Specification và phân trang
        List<Product> products = productRepository.findAll(spec, pageable).getContent();

        if (products.isEmpty()) {
            throw new ListProductEmptyException();
        }

        List<ItsRctProductResponse> productResponses = new ArrayList<>();

        for (Product product : products) {

            // Tính lại giá trị trung bình của Rating
            BigDecimal rating = Optional.ofNullable(reviewRepository.findAverageStarsByProduct(product))
                    .orElse(BigDecimal.ZERO);
            // Lấy thông tin danh mục
            APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(product.getCategory().getId());
            // Lấy thông tin style
            APICustomize<ItsRctStyleResponse> styleResponse = styleService.style(product.getStyle().getId());
            // Lấy thông tin discount nếu có
            APICustomize<ItsRctDiscountResponse> discountResponse = null;
            if (product.getDiscount() != null) {
                discountResponse = discountService.discount(product.getDiscount().getId());
            }
            // Lấy danh sách available variant của sản phẩm
            APICustomize<List<ItsRctProductAvailableVariantResponse>> productAvailableVariants = productAvailableVariantService.productAvailableVariants(product.getId());
            // Lấy danh sách images của sản phẩm
            APICustomize<List<ItsRctImageResponse>> images = imageService.getImages(product.getId(), EntityType.valueOf("PRODUCT"));

            // Tạo response cho sản phẩm
            ItsRctProductResponse productResponse = new ItsRctProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getWeight(),
                    rating,
                    product.getViewCount(),
                    product.getDescription(),
                    product.getDescription(),
                    product.getIntroduction(),
                    product.getHighLightedImageUrl(),
                    images.getResult(),
                    productAvailableVariants.getResult(),
                    categoryResponse.getResult(),
                    styleResponse.getResult(),
                    discountResponse != null ? discountResponse.getResult() : null
            );

            productResponses.add(productResponse);
        }

        // Lấy xong Lưu vào Redis
        for (ItsRctProductResponse item : productResponses) {
            String jsonItem = objectMapper.writeValueAsString(item);
            redisService.addToList(redisKey, jsonItem);
        }

        System.out.println("Lấy từ database");
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctProductResponse> getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id.toString()));

        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        // Tính lại giá trị trung bình của Rating sau mỗi lần get
        BigDecimal rating = Optional.ofNullable(reviewRepository.findAverageStarsByProduct(product)).orElse(BigDecimal.ZERO);

        // Lấy thông tin danh mục
        APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(product.getCategory().getId());

        // Lấy thông tin style
        APICustomize<ItsRctStyleResponse> styleResponse = styleService.style(product.getStyle().getId());

        // Lấy thông tin discount nếu có
        APICustomize<ItsRctDiscountResponse> discountResponse = null;
        if (product.getDiscount() != null) {
            discountResponse = discountService.discount(product.getDiscount().getId());
        }

        // Lấy danh sách available variant của product
        APICustomize<List<ItsRctProductAvailableVariantResponse>> productAvailableVariants = productAvailableVariantService.productAvailableVariants(id);
        // Lấy danh sách images của product
        APICustomize<List<ItsRctImageResponse>> images = imageService.getImages(id, EntityType.valueOf("PRODUCT"));

        ItsRctProductResponse productResponse = new ItsRctProductResponse(
                product.getId(),
                product.getName(),
                product.getWeight(),
                rating,
                product.getViewCount(),
                product.getDescription(),
                product.getDescription(),
                product.getIntroduction(),
                product.getHighLightedImageUrl(),
                images.getResult(),
                productAvailableVariants.getResult(),
                categoryResponse.getResult(),
                styleResponse.getResult(),
                discountResponse != null ? discountResponse.getResult() : null
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productResponse);
    }

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctProductResponse> addProduct(AddProductRequest request) {
        // Lấy thông tin category
        APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(request.getCategoryId());
        Category category = categoryRepository.findById(categoryResponse.getResult().getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId().toString()));

        // Lấy thông tin style
        APICustomize<ItsRctStyleResponse> styleResponse = styleService.style(request.getStyleId());
        Style style = styleRepository.findById(styleResponse.getResult().getStyleId())
                .orElseThrow(() -> new ResourceNotFoundException("Style", "id", request.getStyleId().toString()));

        // Lấy thông tin discount nếu có
        APICustomize<ItsRctDiscountResponse> discountResponse = null;
        Discount discount = null;
        if (request.getDiscountId() != null) {
            discountResponse = discountService.discount(request.getDiscountId());
            discount = discountRepository.findById(discountResponse.getResult().getDiscountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", request.getDiscountId().toString()));
        }

        // Tạo đối tượng Product
        Product newProduct = new Product();
        newProduct.setName(request.getName());
        newProduct.setWeight(request.getWeigh());
        newProduct.setDescription(request.getDescription());
        newProduct.setIntroduction(request.getIntroduction());
        newProduct.setDiscount(discount);
        newProduct.setCategory(category);
        newProduct.setStyle(style);

        // Xử lý ảnh nổi bật (highLightedImageUrl)
        MultipartFile imageFile = request.getHighLightedImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = request.getName().replace(" ", "_") + "_highlighted";
            InputStream inputStream = imageFile.getInputStream();
            minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());
            newProduct.setHighLightedImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName);
        } else {
            throw new BadRequestException("BADREQUEST4001E");
        }

        // Lưu product vào database
        Product savedProduct = productRepository.save(newProduct);

        // Xử lý các ảnh từ danh sách images
        List<ItsRctImageResponse> imageResponses = new ArrayList<>();
        List<MultipartFile> imageFiles = request.getImages(); // Lấy danh sách ảnh từ request

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile image = imageFiles.get(i);
            if (image != null && !image.isEmpty()) {
                String imageName = request.getName().replace(" ", "_") + "_image" + (i + 1);
                InputStream inputStream = image.getInputStream();
                minioService.putObject(BUCKET_NAME, imageName, inputStream, image.getContentType());

                // Tạo URL cho ảnh
                String imageUrl = "/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName;

                // Lưu thông tin ảnh vào cơ sở dữ liệu
                Image imageEntity = new Image();
                imageEntity.setImageUrl(imageUrl);
                imageEntity.setEntityType(EntityType.PRODUCT);
                imageEntity.setEntityId(savedProduct.getId());
                imageRepository.save(imageEntity);

                // Lưu thông tin ảnh vào danh sách
                ItsRctImageResponse imageResponse = new ItsRctImageResponse();
                imageResponse.setId(imageEntity.getId());
                imageResponse.setImageUrl(imageEntity.getImageUrl());
                imageResponse.setEntityType(imageEntity.getEntityType());
                imageResponse.setEntityId(imageEntity.getEntityId());
                imageResponses.add(imageResponse);
            }
        }

        //Xóa cache in Redis to update new data
        redisService.deleteKeysStartingWith("products");

        // Tạo response cho sản phẩm
        ItsRctProductResponse productResponse = new ItsRctProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getWeight(),
                new BigDecimal(0),
                0L,
                savedProduct.getDescription(),
                savedProduct.getDescription(),
                savedProduct.getIntroduction(),
                savedProduct.getHighLightedImageUrl(),
                imageResponses,
                new ArrayList<>(),
                categoryResponse.getResult(),
                styleResponse.getResult(),
                discountResponse != null ? discountResponse.getResult() : null
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), productResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> deleteProduct(Long productId) {
        // Tìm sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        // Xóa ảnh nổi bật khỏi MinIO
        String highlightedImageUrl = product.getHighLightedImageUrl();
        if (highlightedImageUrl != null) {
            String highlightedImageName = highlightedImageUrl.substring(highlightedImageUrl.lastIndexOf("/") + 1);
            minioService.deleteObject(BUCKET_NAME, highlightedImageName);
        }

        // Xóa tất cả ảnh liên quan đến sản phẩm
        List<Image> images = imageRepository.findByEntityIdAndEntityType(productId, EntityType.PRODUCT);
        for (Image image : images) {
            String imageName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
            minioService.deleteObject(BUCKET_NAME, imageName);
        }

        // Xóa sản phẩm
        productRepository.delete(product);

        // Xóa cache in Redis để cập nhật dữ liệu mới
        redisService.deleteKeysStartingWith("products");

        // Trả về phản hồi thành công
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted product with id = " + product.getId());
    }

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctProductResponse> updateProduct(Long productId, AddProductRequest request) {
        // Tìm sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        // Lấy thông tin category
        APICustomize<ItsRctCategoryResponse> categoryResponse = categoryService.category(request.getCategoryId());
        Category category = categoryRepository.findById(categoryResponse.getResult().getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId().toString()));

        // Lấy thông tin style
        APICustomize<ItsRctStyleResponse> styleResponse = styleService.style(request.getStyleId());
        Style style = styleRepository.findById(styleResponse.getResult().getStyleId())
                .orElseThrow(() -> new ResourceNotFoundException("Style", "id", request.getStyleId().toString()));

        // Lấy thông tin discount nếu có
        APICustomize<ItsRctDiscountResponse> discountResponse = null;
        Discount discount = null;
        if (request.getDiscountId() != null) {
            discountResponse = discountService.discount(request.getDiscountId());
            discount = discountRepository.findById(discountResponse.getResult().getDiscountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", request.getDiscountId().toString()));
        }

        // Cập nhật thông tin sản phẩm
        product.setName(request.getName());
        product.setWeight(request.getWeigh());
        product.setDescription(request.getDescription());
        product.setIntroduction(request.getIntroduction());
        product.setDiscount(discount);
        product.setCategory(category);

        // Xử lý ảnh nổi bật (highLightedImageUrl)
        MultipartFile imageFile = request.getHighLightedImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            // Xóa ảnh nổi bật cũ trên MinIO
            if (product.getHighLightedImageUrl() != null) {
                String highlightedImageName = product.getHighLightedImageUrl().substring(product.getHighLightedImageUrl().lastIndexOf("/") + 1);
                minioService.deleteObject(BUCKET_NAME, highlightedImageName);
            }
            // Tải ảnh mới lên MinIO
            String imageName = request.getName().replace(" ", "_") + "_highlighted";
            InputStream inputStream = imageFile.getInputStream();
            minioService.putObject(BUCKET_NAME, imageName, inputStream, imageFile.getContentType());
            product.setHighLightedImageUrl("/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName);
        }

        // Xóa các ảnh cũ liên quan đến sản phẩm
        List<Image> oldImages = imageRepository.findByEntityIdAndEntityType(productId, EntityType.PRODUCT);
        for (Image image : oldImages) {
            String imageName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
            minioService.deleteObject(BUCKET_NAME, imageName);
            imageRepository.delete(image);
        }

        // Tải ảnh mới lên MinIO và lưu vào cơ sở dữ liệu
        List<ItsRctImageResponse> imageResponses = new ArrayList<>();
        List<MultipartFile> imageFiles = request.getImages();
        if (imageFiles != null) {
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile image = imageFiles.get(i);
                if (image != null && !image.isEmpty()) {
                    String imageName = request.getName().replace(" ", "_") + "_image" + (i + 1);
                    InputStream inputStream = image.getInputStream();
                    minioService.putObject(BUCKET_NAME, imageName, inputStream, image.getContentType());

                    // Tạo URL cho ảnh
                    String imageUrl = "/api/bff/its-rct/v1/public/image/" + BUCKET_NAME + "/" + imageName;

                    // Lưu thông tin ảnh vào cơ sở dữ liệu
                    Image imageEntity = new Image();
                    imageEntity.setImageUrl(imageUrl);
                    imageEntity.setEntityType(EntityType.PRODUCT);
                    imageEntity.setEntityId(product.getId());
                    imageRepository.save(imageEntity);

                    // Lưu thông tin ảnh vào danh sách
                    ItsRctImageResponse imageResponse = new ItsRctImageResponse();
                    imageResponse.setId(imageEntity.getId());
                    imageResponse.setImageUrl(imageEntity.getImageUrl());
                    imageResponse.setEntityType(imageEntity.getEntityType());
                    imageResponse.setEntityId(imageEntity.getEntityId());
                    imageResponses.add(imageResponse);
                }
            }
        }

        // Lưu sản phẩm đã cập nhật vào cơ sở dữ liệu
        productRepository.save(product);

        // Xóa cache in Redis để cập nhật dữ liệu mới
        redisService.deleteKeysStartingWith("products");

        // Tạo response cho sản phẩm
        ItsRctProductResponse productResponse = new ItsRctProductResponse(
                product.getId(),
                product.getName(),
                product.getWeight(),
                new BigDecimal(0),
                product.getViewCount(),
                product.getDescription(),
                product.getDescription(),
                product.getIntroduction(),
                product.getHighLightedImageUrl(),
                imageResponses,
                new ArrayList<>(),
                categoryResponse.getResult(),
                styleResponse.getResult(),
                discountResponse != null ? discountResponse.getResult() : null
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productResponse);
    }
}
