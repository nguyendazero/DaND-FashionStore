package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddToCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.*;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.exception.UnauthorizedException;
import com.haibazo_bff_its_rct_webapi.model.CartItem;
import com.haibazo_bff_its_rct_webapi.model.Discount;
import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.repository.CartItemRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductAvailableVariantRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
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
    public APICustomize<String> removeFromCart(RemoveFromCartRequest request, String authorizationHeader) {
        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        if (userResponse == null) {
            throw new UnauthorizedException();
        }

        // Lấy thông tin người dùng
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        // Lấy variant sản phẩm và kiểm tra trong giỏ hàng
        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository.findById(request.getProductAvailableVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", request.getProductAvailableVariantId().toString()));

        CartItem cartItem = cartItemRepository.findByUserIdAndProductAvailableVariantId(user.getId(), productAvailableVariant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "UserId and ProductAvailableVariantId",
                        "Your cart does not have the product with ID: " + productAvailableVariant.getId() + "."));
        
        cartItemRepository.delete(cartItem);
        
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Product removed from cart successfully");
    }
}
