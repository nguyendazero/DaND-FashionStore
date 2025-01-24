package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;

public interface WishlistService {

    public APICustomize<String> addWishes(Long productId, String authorizationHeader);

    public APICustomize<String> deleteWishes(Long productId, String authorizationHeader);

}
