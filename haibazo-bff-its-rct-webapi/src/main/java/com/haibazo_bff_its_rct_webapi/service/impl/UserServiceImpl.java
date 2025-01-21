package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.UserRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.*;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.*;
import com.haibazo_bff_its_rct_webapi.service.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;
    private final WishListRepository wishListRepository;
    private final CategoryService categoryService;
    private final DiscountService discountService;
    private final ImageService imageService;
    private final ReviewRepository reviewRepository;
    private final ProductAvailableVariantService productAvailableVariantService;
    private final StyleService styleService;

    @PostConstruct // Khởi tạo WebClient khi bean được tạo
    public void init() {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctUserResponse> getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        // Gọi account-service để lấy thông tin tài khoản
        ItsRctUserResponse userResponse = webClient.get()
                .uri("/api/bff/its-rct/v1/user/account/{id}", user.getHaibazoAccountId())
                .retrieve()
                .bodyToMono(ItsRctUserResponse.class)
                .block();

        // Gộp thông tin từ User và Account
        if (userResponse != null) {
            userResponse.setId(user.getId());
            userResponse.setHaibazoAuthAlias(user.getHaibazoAccountId());
        }

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), userResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public Long create(UserRequest request) {
        User user = new User();
        user.setHaibazoAccountId(request.getHaibazoAccountId());
        return userRepository.save(user).getId();
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCouponResponse>> couponsByUserId(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        // Lấy danh sách các userCoupon liên quan đến user
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(id);

        List<ItsRctCouponResponse> couponResponses = userCoupons.stream()
                .map(userCoupon -> new ItsRctCouponResponse(
                      userCoupon.getCoupon().getId(),
                        userCoupon.getCoupon().getCode(),
                        userCoupon.getCoupon().getType(),
                        userCoupon.getCoupon().getStartDate(),
                        userCoupon.getCoupon().getDiscount(),
                        userCoupon.getCoupon().getEndDate(),
                        userCoupon.getCoupon().getMinSpend(),
                        userCoupon.getCoupon().getCreatedAt(),
                        userCoupon.getCoupon().getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), couponResponses);
    }

    @Override
    public APICustomize<List<ItsRctProductResponse>> wishListByUserId(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        List<WishList> productsWishList = wishListRepository.findByUserId(id);
        List<ItsRctProductResponse> productResponses = new ArrayList<>();

        for (Product product : productsWishList.stream().map(WishList::getProduct).toList()) {

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

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productResponses);
    }

    @Override
    public APICustomize<List<ItsRctAddressResponse>> getAddressesByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        List<Address> addresses = addressRepository.findByUserId(userId);

        List<ItsRctAddressResponse> addressResponses = addresses.stream()
                .map(address -> {
                    ItsRctAddressResponse response = new ItsRctAddressResponse();
                    response.setId(address.getId());
                    response.setDisplayName(address.getDisplayName());
                    response.setFirstName(address.getFirstName());
                    response.setLastName(address.getLastName());
                    response.setEmail(address.getEmail());
                    response.setPhone(address.getPhone());
                    response.setStreetAddress(address.getStreetAddress());
                    response.setWardId(address.getWard().getId());
                    response.setUserId(userId);
                    response.setCreatedAt(address.getCreatedAt());
                    response.setUpdatedAt(address.getUpdatedAt());
                    return response;
                })
                .toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), addressResponses);
    }

}