package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.ProductRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.repository.WishListRepository;
import com.haibazo_bff_its_rct_webapi.service.WishlistService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishlistService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishListRepository wishListRepository;
    private final TokenUtil tokenUtil;

    @Override
    public APICustomize<String> addWishes(Long productId, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        assert userResponse != null;
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        if (wishListRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new ResourceAlreadyExistsException("Wishes", "UserId và ProductId",
                    "User ID: " + user.getId() + ", Product ID: " + product.getId());
        }

        WishList wishList = new WishList();
        wishList.setUser(user);
        wishList.setProduct(product);
        wishListRepository.save(wishList);

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), "Wishes added successfully");
    }

    @Override
    @Transactional
    public APICustomize<String> deleteWishes(Long productId, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        assert userResponse != null;
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));

        if (!wishListRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new ResourceNotFoundException("Wishes", "UserId và ProductId",
                    "User with ID: " + user.getId() + " does not have the product with ID: " + product.getId() + " in wishlist.");
        }

        wishListRepository.deleteByUserIdAndProductId(user.getId(), product.getId());

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Wishes deleted successfully");
    }
}
