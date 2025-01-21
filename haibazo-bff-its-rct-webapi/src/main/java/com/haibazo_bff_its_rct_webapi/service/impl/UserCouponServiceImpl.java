package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserCouponRequest;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.CouponRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserCouponRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.UserCouponService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<Void> add(AddUserCouponRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", request.getCouponId().toString()));


        if (userCouponRepository.existsByUserIdAndCouponId(user.getId(), coupon.getId())){
            throw new ResourceAlreadyExistsException("UserCoupon", "UserId va CouponId",
                    "User ID: " + user.getId() + ", Coupon ID: " + coupon.getId());
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setUsed(false);
        userCouponRepository.save(userCoupon);

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), null);
    }
}
