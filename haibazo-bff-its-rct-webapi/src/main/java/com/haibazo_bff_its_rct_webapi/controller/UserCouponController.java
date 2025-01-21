package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserCouponRequest;
import com.haibazo_bff_its_rct_webapi.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping("/admin/user-coupon")
    public ResponseEntity<?> create(@RequestBody AddUserCouponRequest request){
        userCouponService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
