package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCouponRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCouponResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Coupon;
import com.haibazo_bff_its_rct_webapi.repository.CouponRepository;
import com.haibazo_bff_its_rct_webapi.service.CouponService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctCouponResponse>> coupons() {
        List<ItsRctCouponResponse> responses = couponRepository.findAll().stream()
                .map(coupon -> new ItsRctCouponResponse(
                        coupon.getId(),
                        coupon.getCode(),
                        coupon.getType(),
                        coupon.getStartDate(),
                        coupon.getDiscount(),
                        coupon.getEndDate(),
                        coupon.getMinSpend(),
                        coupon.getCreatedAt(),
                        coupon.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), responses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCouponResponse> create(AddCouponRequest request) {

        if (couponRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("Coupon", "code", request.getCode());
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDiscount(request.getDiscount());
        coupon.setType(request.getType());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setMinSpend(request.getMinSpend());
        Coupon savedCoupon = couponRepository.save(coupon);

        ItsRctCouponResponse response = new ItsRctCouponResponse(
                savedCoupon.getId(),
                savedCoupon.getCode(),
                savedCoupon.getType(),
                savedCoupon.getStartDate(),
                savedCoupon.getDiscount(),
                savedCoupon.getEndDate(),
                savedCoupon.getMinSpend(),
                savedCoupon.getCreatedAt(),
                savedCoupon.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCouponResponse> coupon(Long id) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id.toString()));

        ItsRctCouponResponse response = new ItsRctCouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getType(),
                coupon.getStartDate(),
                coupon.getDiscount(),
                coupon.getEndDate(),
                coupon.getMinSpend(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id.toString()));

        couponRepository.delete(coupon);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted coupon with id = " + id);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctCouponResponse> update(Long id, AddCouponRequest request) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id.toString()));

        if (couponRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("Coupon", "code", request.getCode());
        }

        coupon.setCode(request.getCode());
        coupon.setDiscount(request.getDiscount());
        coupon.setType(request.getType());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setMinSpend(request.getMinSpend());
        coupon.setUpdatedAt(LocalDateTime.now());
        Coupon savedCoupon = couponRepository.save(coupon);

        ItsRctCouponResponse response = new ItsRctCouponResponse(
                savedCoupon.getId(),
                savedCoupon.getCode(),
                savedCoupon.getType(),
                savedCoupon.getStartDate(),
                savedCoupon.getDiscount(),
                savedCoupon.getEndDate(),
                savedCoupon.getMinSpend(),
                savedCoupon.getCreatedAt(),
                savedCoupon.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);

    }
}
