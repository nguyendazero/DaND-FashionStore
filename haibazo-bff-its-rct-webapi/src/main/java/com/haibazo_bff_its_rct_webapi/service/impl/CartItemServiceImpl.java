package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddToCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDiscountResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductAvailableVariantResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductVariantResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.CartItem;
import com.haibazo_bff_its_rct_webapi.model.Discount;
import com.haibazo_bff_its_rct_webapi.model.ProductAvailableVariant;
import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.repository.CartItemRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductAvailableVariantRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductAvailableVariantRepository productAvailableVariantRepository;


    @Override
    public APICustomize<List<ItsRctCartResponse>> getCartItemByUserid(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        List<ItsRctCartResponse> cartResponses = cartItems.stream()
                .map(cartItem -> {
                    // Lấy thông tin từ ProductAvailableVariant
                    ProductAvailableVariant variant = cartItem.getProductAvailableVariant();

                    // Tạo đối tượng ItsRctProductAvailableVariantResponse
                    ItsRctProductAvailableVariantResponse productAvailableVariantResponse = new ItsRctProductAvailableVariantResponse();
                    productAvailableVariantResponse.setId(variant.getId());
                    productAvailableVariantResponse.setHighLightedImageUrl(variant.getHighLightedImageUrl());
                    productAvailableVariantResponse.setPrice(variant.getPrice());
                    productAvailableVariantResponse.setStock(variant.getStock());
                    productAvailableVariantResponse.setProductId(variant.getProduct().getId());

                    // Chuyển đổi Discount
                    Discount discount = variant.getDiscount();
                    ItsRctDiscountResponse discountResponse = null;
                    if (discount != null) {
                        discountResponse = new ItsRctDiscountResponse();
                        discountResponse.setDiscountId(discount.getId());
                        discountResponse.setDiscountValue(discount.getDiscountValue());
                        discountResponse.setDateEndSale(discount.getDateEndSale());
                    }
                    productAvailableVariantResponse.setDiscount(discountResponse);

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
                    return new ItsRctCartResponse(userId, productAvailableVariantResponse, cartItem.getQuantity());
                })
                .toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), cartResponses);
    }

    @Override
    public APICustomize<String> addToCart(AddToCartRequest request) {
        // Lấy thông tin người dùng
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        // Lấy thông tin variant sản phẩm
        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository.findById(request.getProductAvailableVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", request.getProductAvailableVariantId().toString()));

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductAvailableVariantId(user.getId(), productAvailableVariant.getId());

        if (existingCartItem != null) {
            // Nếu đã có, cập nhật số lượng
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingCartItem);
        } else {
            // Nếu chưa có, tạo mới CartItem
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProductAvailableVariant(productAvailableVariant);
            newCartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newCartItem);
        }

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Product added to cart successfully");
    }

    @Override
    public APICustomize<String> removeFromCart(RemoveFromCartRequest request) {
        // Lấy thông tin người dùng
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        // Lấy thông tin variant sản phẩm
        ProductAvailableVariant productAvailableVariant = productAvailableVariantRepository.findById(request.getProductAvailableVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductAvailableVariant", "id", request.getProductAvailableVariantId().toString()));

        // Kiểm tra xem sản phẩm có trong giỏ hàng không
        CartItem cartItem = cartItemRepository.findByUserIdAndProductAvailableVariantId(user.getId(), productAvailableVariant.getId());

        if (cartItem == null) {
            // Nếu không có trong giỏ hàng, ném ngoại lệ
            throw new ResourceNotFoundException("CartItem", "UserId and ProductAvailableVariantId",
                    "User with ID: " + user.getId() + " does not have the productAvailableVariant with ID: " + productAvailableVariant.getId() + " in cart.");
        }

        // Xóa sản phẩm khỏi giỏ hàng
        cartItemRepository.delete(cartItem);

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Product removed from cart successfully");
    }
}
