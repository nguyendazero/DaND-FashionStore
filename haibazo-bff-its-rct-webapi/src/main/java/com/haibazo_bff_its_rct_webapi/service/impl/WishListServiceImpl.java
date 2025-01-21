package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddWishesRequest;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.CouponRepository;
import com.haibazo_bff_its_rct_webapi.repository.ProductRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.repository.WishListRepository;
import com.haibazo_bff_its_rct_webapi.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishlistService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishListRepository wishListRepository;

    @Override
    public APICustomize<String> addWishes(AddWishesRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId().toString()));


        if (wishListRepository.existsByUserIdAndProductId(user.getId(), product.getId())){
            throw new ResourceAlreadyExistsException("Wishes", "UserId va ProductId",
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
    public APICustomize<String> deleteWishes(AddWishesRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId().toString()));

        if (!wishListRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new ResourceNotFoundException("Wishes", "UserId and ProductId",
                    "User with ID: " + user.getId() + " does not have the product with ID: " + product.getId() + " in wishlist.");
        }

        wishListRepository.deleteByUserIdAndProductId(user.getId(), product.getId());

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Wishes deleted successfully");

    }
}
