package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddToCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.*;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.*;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.*;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductAvailableVariantRepository productAvailableVariantRepository;
    private final TokenUtil tokenUtil;


    @Override
    public APICustomize<List<ItsRctCartResponse>> getCartItems(String authorizationHeader) {
        // Lấy JWT từ header và xác thực người dùng
        ItsRctUserResponse userResponse = (authorizationHeader != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(authorizationHeader))
                : null;

        assert userResponse != null;
        List<CartItem> cartItems = cartItemRepository.findByUserId(userResponse.getId());

        // Chuyển đổi CartItem thành ItsRctCartResponse
        List<ItsRctCartResponse> cartResponses = cartItems.stream()
                .map(cartItem -> {
                    // Lấy thông tin từ ProductAvailableVariant
                    ProductAvailableVariant variant = cartItem.getProductAvailableVariant();

                    // Tạo đối tượng ItsRctProductAvailableVariantResponse
                    ItsRctProductAvailableVariantResponse productAvailableVariantResponse = new ItsRctProductAvailableVariantResponse();
                    productAvailableVariantResponse.setId(variant.getId());
                    productAvailableVariantResponse.setHighLightedImageUrl(variant.getHighLightedImageUrl());

                    // Lấy giá gốc
                    BigDecimal originalPrice = variant.getPrice();
                    BigDecimal finalPrice = originalPrice;

                    // Chuyển đổi Discount
                    Discount discount = variant.getDiscount();
                    ItsRctDiscountResponse discountResponse = null;
                    if (discount != null) {
                        discountResponse = new ItsRctDiscountResponse();
                        discountResponse.setDiscountId(discount.getId());
                        
                        BigDecimal discountPercentage = discount.getDiscountValue();
                        BigDecimal discountAmount = originalPrice.multiply(discountPercentage.divide(new BigDecimal("100"))); // Tính số tiền giảm
                        finalPrice = originalPrice.subtract(discountAmount); // Tính giá cuối
                        discountResponse.setDiscountValue(discountPercentage); // Lưu phần trăm giảm
                        discountResponse.setDateEndSale(discount.getDateEndSale());
                    }
                    productAvailableVariantResponse.setDiscount(discountResponse);
                    productAvailableVariantResponse.setPrice(finalPrice);
                    productAvailableVariantResponse.setStock(variant.getStock());
                    productAvailableVariantResponse.setProductId(variant.getProduct().getId());

                    // Chuyển đổi ProductVariants
                    List<ItsRctProductVariantResponse> productVariantResponses = variant.getProductVariants().stream()
                            .map(productVariant -> {
                                ItsRctProductVariantResponse variantResponse = new ItsRctProductVariantResponse();
                                variantResponse.setVariantKey(productVariant.getVariantGroupKey().getName());
                                variantResponse.setValue(productVariant.getValue());
                                return variantResponse;
                            })
                            .toList();
                    productAvailableVariantResponse.setProductVariants(productVariantResponses);

                    // Tạo đối tượng ItsRctCartResponse
                    return new ItsRctCartResponse(productAvailableVariantResponse, cartItem.getQuantity());
                })
                .toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), cartResponses);
    }

    @Override
    public BigDecimal calculateTotalPrice(String authorizationHeader) {
        ItsRctUserResponse userResponse = (authorizationHeader != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(authorizationHeader))
                : null;

        assert userResponse != null;
        List<CartItem> cartItems = cartItemRepository.findByUserId(userResponse.getId());

        // Tính tổng giá trị của giỏ hàng
        return cartItems.stream()
                .map(cartItem -> {
                    ProductAvailableVariant variant = cartItem.getProductAvailableVariant();
                    BigDecimal originalPrice = variant.getPrice();
                    BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
                    BigDecimal finalPrice = originalPrice;

                    // Xử lý discount nếu có
                    Discount discount = variant.getDiscount();
                    if (discount != null) {
                        BigDecimal discountPercentage = discount.getDiscountValue(); // Phần trăm giảm giá
                        BigDecimal discountAmount = originalPrice.multiply(discountPercentage.divide(new BigDecimal("100")));
                        finalPrice = originalPrice.subtract(discountAmount); // Tính giá sau khi giảm
                    }

                    return finalPrice.multiply(quantity); // Tính tổng giá trị cho item này
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Cộng dồn tất cả giá trị
    }

    @Override
    public APICustomize<String> addToCart(Long variantId, String authorizationHeader) {
        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;
        
        // Lấy thông tin người dùng
        assert userResponse != null;
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        // Lấy variant sản phẩm
        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", variantId.toString()));

        // Kiểm tra và cập nhật hoặc thêm sản phẩm vào giỏ hàng
        Optional<CartItem> optionalCartItem = cartItemRepository.findByUserIdAndProductAvailableVariantId(user.getId(), productAvailableVariant.getId());

        if (optionalCartItem.isPresent()) {
            // Cập nhật số lượng nếu đã có
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
        } else {
            // Tạo mới CartItem nếu chưa có
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProductAvailableVariant(productAvailableVariant);
            newCartItem.setQuantity(1);
            cartItemRepository.save(newCartItem);
        }

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Product added to cart successfully");
    }

    @Override
    public APICustomize<String> removeFromCart(Long variantId, String authorizationHeader) {
        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra người dùng đã được xác thực
        if (userResponse == null) {
            throw new UnauthorizedException();
        }

        // Lấy thông tin người dùng
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        // Lấy variant sản phẩm dựa trên ID từ request
        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", variantId.toString()));

        // Tìm CartItem trong giỏ hàng của người dùng
        CartItem cartItem = cartItemRepository.findByUserIdAndProductAvailableVariantId(user.getId(), productAvailableVariant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "UserId and ProductAvailableVariantId",
                        "Your cart does not have the product with ID: " + productAvailableVariant.getId() + "."));

        // Xóa CartItem khỏi giỏ hàng
        cartItemRepository.delete(cartItem);

        // Trả về phản hồi thành công
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Product removed from cart successfully");
    }

    @Override
    public APICustomize<String> changeQuantity(Long variantId, String authorizationHeader, int change) {
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        if (userResponse == null) {
            throw new UnauthorizedException();
        }

        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", variantId.toString()));

        CartItem cartItem = cartItemRepository.findByUserIdAndProductAvailableVariantId(user.getId(), productAvailableVariant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "UserId and ProductAvailableVariantId",
                        "Your cart does not have the product with ID: " + productAvailableVariant.getId() + "."));

        long newQuantity = cartItem.getQuantity() + change;

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Quantity updated successfully");
    }

    @Override
    public BigDecimal applyCoupon(String code, ItsRctUserResponse userResponse, Model model) {
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        // Tìm coupon theo mã
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));

        // Kiểm tra xem người dùng đã sử dụng coupon này chưa
        UserCoupon userCoupon = userCouponRepository.findByUserAndCoupon(user, coupon)
                .orElse(null);

        if (userCoupon != null && userCoupon.isUsed()) {
            throw new CouponAlreadyUsedException();
        }

        // Kiểm tra thời gian hiệu lực của coupon
        if (LocalDateTime.now().isAfter(coupon.getEndDate())) {
            throw new CouponExpiredException();
        }

        // Lấy danh sách hàng trong giỏ
        List<CartItem> cartItems = cartItemRepository.findByUserId(userResponse.getId());
        if (cartItems.isEmpty()) {
            throw new CartEmptyException(userResponse.getId());
        }

        // Tính toán tổng giá trị đơn hàng
        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem -> cartItem.getProductAvailableVariant().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Kiểm tra giá trị tối thiểu cần chi tiêu
        if (totalPrice.compareTo(coupon.getMinSpend()) < 0) {
            throw new MinSpendCouponException(coupon.getMinSpend());
        }

        // Tính toán giá trị đơn hàng sau khi áp dụng coupon
        BigDecimal discountAmount = totalPrice.multiply(coupon.getDiscount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
        totalPrice = totalPrice.subtract(discountAmount);

        // Cập nhật lại tổng giá trị cho mô hình
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("items", cartItems);
        model.addAttribute("discountAmount", discountAmount);

        return totalPrice;
    }
}
