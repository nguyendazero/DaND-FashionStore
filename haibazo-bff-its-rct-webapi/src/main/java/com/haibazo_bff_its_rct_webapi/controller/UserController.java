package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.UserRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCouponResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/user/coupons")
    public ResponseEntity<?> couponsByUserId(@RequestParam Long id){
        APICustomize<List<ItsRctCouponResponse>> response = userService.couponsByUserId(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/user/wishes")
    public ResponseEntity<?> wishes(@RequestParam Long id){
        APICustomize<List<ItsRctProductResponse>> response = userService.wishListByUserId(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/user")
    public ResponseEntity<?> user(@RequestParam Long id){
        APICustomize<ItsRctUserResponse> response = userService.getUserById(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/user/create")
    public ResponseEntity<?> signUp(@RequestBody UserRequest userRequest) {
        Long userId = userService.create(userRequest);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/user/user/addresses")
    public ResponseEntity<?> addresses(@RequestParam Long id){
        APICustomize<List<ItsRctAddressResponse>> response = userService.getAddressesByUserId(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/user")
    public ResponseEntity<?> delete(@RequestParam Long id){
        APICustomize<String> response = userService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }


}
