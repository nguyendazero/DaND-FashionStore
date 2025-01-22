package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserCouponRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddWishesRequest;
import com.haibazo_bff_its_rct_webapi.model.WishList;
import com.haibazo_bff_its_rct_webapi.service.UserCouponService;
import com.haibazo_bff_its_rct_webapi.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class WishListController {

    private final WishlistService wishlistService;

    @PostMapping("/user/wishes")
    public ResponseEntity<?> create(@RequestBody AddWishesRequest request){
        APICustomize<String> response = wishlistService.addWishes(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/user/wishes")
    public ResponseEntity<?> delete(@RequestBody AddWishesRequest request){
        APICustomize<String> response = wishlistService.deleteWishes(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
