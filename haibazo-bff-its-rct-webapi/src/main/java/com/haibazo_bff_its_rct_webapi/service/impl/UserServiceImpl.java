package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.UpdateAccountRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.UpdateInfoRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.UserRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.*;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.event.DeleteUserEvent;
import com.haibazo_bff_its_rct_webapi.exception.ErrorDeleteUserException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.exception.UnauthorizedException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.*;
import com.haibazo_bff_its_rct_webapi.service.*;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final NotificationRepository notificationRepository;
    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;
    private final WishListRepository wishListRepository;
    private final CategoryService categoryService;
    private final DiscountService discountService;
    private final ImageService imageService;
    private final ReviewRepository reviewRepository;
    private final ProductAvailableVariantService productAvailableVariantService;
    private final StyleService styleService;
    private final TokenUtil tokenUtil;
    private final MinioServiceImpl minioService;
    private final String BUCKET_NAME = "users";

    //Kafka
    private final KafkaTemplate<String, DeleteUserEvent> kafkaTemplate;


    @PostConstruct // Khởi tạo WebClient khi bean được tạo
    public void init() {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctUserResponse> getUserByToken(String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        if (token == null) {
            throw new UnauthorizedException();
        }

        // Lấy ID người dùng từ token
        Long haizaoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
        User user = userRepository.findByHaibazoAccountId(haizaoAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "haizaoAccountId", haizaoAccountId.toString()));

        // Gọi account-service để lấy thông tin tài khoản
        ItsRctUserResponse userResponse = webClient.get()
                .uri("/api/bff/its-rct/v1/account/user/account/{id}", user.getHaibazoAccountId())
                .retrieve()
                .bodyToMono(ItsRctUserResponse.class)
                .block();

        // Gộp thông tin từ User và Account
        if (userResponse != null) {
            userResponse.setId(user.getId());
            userResponse.setHaibazoAccountId(user.getHaibazoAccountId());
        }

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), userResponse);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public Long create(UserRequest request) {
        User user = new User();
        user.setHaibazoAccountId(request.getHaibazoAccountId());
        Notification notification = notificationRepository.findById(request.getNotificationId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", request.getNotificationId().toString()));

        user.setNotification(notification);
        return userRepository.save(user).getId();
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCouponResponse>> couponsByToken(String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        if (token == null) {
            throw new UnauthorizedException();
        }

        // Lấy ID người dùng từ token
        Long haizaoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
        User user = userRepository.findByHaibazoAccountId(haizaoAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "haizaoAccountId", haizaoAccountId.toString()));

        // Lấy danh sách các userCoupon liên quan đến user
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(user.getId());

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
    public APICustomize<List<ItsRctProductResponse>> wishListByToken(String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        if (token == null) {
            throw new UnauthorizedException();
        }

        // Lấy ID người dùng từ token
        Long haizaoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
        User user = userRepository.findByHaibazoAccountId(haizaoAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "haizaoAccountId", haizaoAccountId.toString()));

        // Lấy danh sách sản phẩm trong wishlist của người dùng
        List<WishList> productsWishList = wishListRepository.findByUserId(user.getId());
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

            BigDecimal lowestPrice = product.getProductAvailableVariants().stream()
                    .map(ProductAvailableVariant::getPrice)
                    .filter(Objects::nonNull) // Lọc các giá null nếu có
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO); // Giá mặc định nếu không có
            
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
                    discountResponse != null ? discountResponse.getResult() : null,
                    lowestPrice
            );

            productResponses.add(productResponse);
        }

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), productResponses);
    }

    @Override
    public APICustomize<List<ItsRctAddressResponse>> getAddressesByToken(String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        if (token == null) {
            throw new UnauthorizedException();
        }

        // Lấy ID người dùng từ token
        Long haizaoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
        User user = userRepository.findByHaibazoAccountId(haizaoAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "haizaoAccountId", haizaoAccountId.toString()));

        // Lấy danh sách địa chỉ của người dùng
        List<Address> addresses = addressRepository.findByUserId(user.getId());

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
                    response.setUserId(user.getId());
                    response.setCreatedAt(address.getCreatedAt());
                    response.setUpdatedAt(address.getUpdatedAt());
                    return response;
                })
                .toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), addressResponses);
    }

    @Override
    public APICustomize<String> delete(Long id, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        if (token == null) {
            throw new UnauthorizedException();
        }

        // Lấy ID người dùng từ token
        Long haizaoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
        ItsRctUserResponse currentUserResponse = webClient.get()
                .uri("/api/bff/its-rct/v1/account/user/account/{id}", haizaoAccountId)
                .retrieve()
                .bodyToMono(ItsRctUserResponse.class)
                .block();

        // Kiểm tra thông tin người dùng hiện tại và quyền hạn
        if (currentUserResponse == null || currentUserResponse.getUsername() == null ||
                !("admin".equals(currentUserResponse.getUsername()) && currentUserResponse.getRole().contains("ROLE_ADMIN"))) {
            throw new ErrorDeleteUserException();
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        // Gửi sự kiện DeleteUserEvent đến Kafka và xóa người dùng
        kafkaTemplate.send("userDeletionTopic", new DeleteUserEvent(user.getHaibazoAccountId()));
        userRepository.delete(user);

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted user with id = " + id);
    }

    @Override
    public APICustomize<ItsRctUserResponse> updateUserInfo(String authorizationHeader, UpdateInfoRequest request) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        if (token == null) {
            throw new UnauthorizedException();
        }

        // Lấy người dùng từ token
        Long haizaoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
        User user = userRepository.findByHaibazoAccountId(haizaoAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "haizaoAccountId", haizaoAccountId.toString()));

        String avatarUrl = null;

        // Lưu avatar vào MinIO nếu có
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            try {
                String avatarName = "avatar_" + user.getId() + "_" + System.currentTimeMillis();
                InputStream inputStream = request.getAvatar().getInputStream();
                // Lưu avatar vào MinIO
                minioService.putObject(BUCKET_NAME, avatarName, inputStream, request.getAvatar().getContentType());
                avatarUrl = "/api/bff/its-rct/v1/ecommerce/public/image/" + BUCKET_NAME + "/" + avatarName;
            } catch (Exception e) {
                throw new RuntimeException("Error while uploading avatar: " + e.getMessage(), e);
            }
        }

        // Tạo đối tượng UpdateInfoRequest với avatarUrl
        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
        updateAccountRequest.setAvatar(avatarUrl);
        updateAccountRequest.setFullName(request.getFullName());
        updateAccountRequest.setStatus(request.getStatus());

        // Gọi account-service để cập nhật thông tin tài khoản
        ItsRctUserResponse updatedUserResponse = webClient.put()
                .uri("/api/bff/its-rct/v1/account/user/account/update/{haibazoAccountId}", haizaoAccountId)
                .bodyValue(updateAccountRequest) // Gửi đối tượng đã cập nhật
                .retrieve()
                .bodyToMono(ItsRctUserResponse.class)
                .block(); // Chờ cho phản hồi

        // Kiểm tra xem cập nhật có thành công không
        if (updatedUserResponse == null) {
            throw new RuntimeException("Failed to update user info in account-service.");
        }

        // Gọi lại thông tin người dùng từ account-service để lấy toàn bộ thông tin
        ItsRctUserResponse finalUserResponse = webClient.get()
                .uri("/api/bff/its-rct/v1/account/user/account/{id}", user.getHaibazoAccountId())
                .retrieve()
                .bodyToMono(ItsRctUserResponse.class)
                .block();

        // Gộp thông tin từ User và Account
        if (finalUserResponse != null) {
            finalUserResponse.setId(user.getId());
            finalUserResponse.setHaibazoAccountId(user.getHaibazoAccountId());
        }

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), finalUserResponse);
    }


}