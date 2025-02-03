package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.UpdateInfoRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.UserRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCouponResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/user/coupons")
    public ResponseEntity<?> couponsByUser(HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<List<ItsRctCouponResponse>> response = userService.couponsByToken(authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/user/wishes")
    public ResponseEntity<?> wishes(HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<List<ItsRctProductResponse>> response = userService.wishListByToken(authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/user")
    public ResponseEntity<?> user(HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<ItsRctUserResponse> response = userService.getUserByToken(authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/user/create")
    public ResponseEntity<?> signUp(@RequestBody UserRequest userRequest) {
        Long userId = userService.create(userRequest);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/user/user/addresses")
    public ResponseEntity<?> addresses(HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<List<ItsRctAddressResponse>> response = userService.getAddressesByToken(authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/user")
    public ResponseEntity<?> delete(@RequestParam Long id, HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = userService.delete(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/user/update-info")
    public ResponseEntity<?> updateUserInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @ModelAttribute UpdateInfoRequest request) {
        APICustomize<ItsRctUserResponse> response = userService.updateUserInfo(authorizationHeader, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }


}
