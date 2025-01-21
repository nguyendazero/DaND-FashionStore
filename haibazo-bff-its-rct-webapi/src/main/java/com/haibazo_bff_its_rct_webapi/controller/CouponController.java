package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCouponRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCouponResponse;
import com.haibazo_bff_its_rct_webapi.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/admin/coupon/coupons")
    public ResponseEntity<?> coupons() {
        APICustomize<List<ItsRctCouponResponse>> response = couponService.coupons();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/coupon")
    public ResponseEntity<?> create(@RequestBody AddCouponRequest request) {
        APICustomize<ItsRctCouponResponse> response = couponService.create(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/coupon")
    public ResponseEntity<?> update(@RequestParam Long id, @RequestBody AddCouponRequest request) {
        APICustomize<ItsRctCouponResponse> response = couponService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/coupon")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = couponService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/coupon")
    public ResponseEntity<?> coupon(@RequestParam Long id) {
        APICustomize<ItsRctCouponResponse> response = couponService.coupon(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
