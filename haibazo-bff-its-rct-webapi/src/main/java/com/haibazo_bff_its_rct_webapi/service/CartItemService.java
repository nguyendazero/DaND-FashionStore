package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

public interface CartItemService {

    public BigDecimal calculateTotalPrice(String authorizationHeader);

    public APICustomize<List<ItsRctCartResponse>> getCartItems(String authorizationHeader);

    public APICustomize<String> addToCart(Long variantId, String authorizationHeader);

    public APICustomize<String> removeFromCart(Long variantId, String authorizationHeader);

    public APICustomize<String> changeQuantity(Long variantId, String authorizationHeader, int change);

    public BigDecimal applyCoupon(String code, ItsRctUserResponse userResponse, Model model);


}
