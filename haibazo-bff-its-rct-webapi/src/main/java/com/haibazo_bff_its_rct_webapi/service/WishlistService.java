package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddWishesRequest;

public interface WishlistService {

    public APICustomize<String> addWishes(AddWishesRequest request);

    public APICustomize<String> deleteWishes(AddWishesRequest request);

}
