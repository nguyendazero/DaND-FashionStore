package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddToCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
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
public class CartItemController {

    private final CartItemService cartItemService;

    @GetMapping("/user/cart-item/cart-items")
    public ResponseEntity<?> cartItems( HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<List<ItsRctCartResponse>> response = cartItemService.getCartItems(authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/user/cart-item")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request, HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = cartItemService.addToCart(request, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/user/cart-item")
    public ResponseEntity<?> removeFromCart(@RequestBody RemoveFromCartRequest request, HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = cartItemService.removeFromCart(request, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
