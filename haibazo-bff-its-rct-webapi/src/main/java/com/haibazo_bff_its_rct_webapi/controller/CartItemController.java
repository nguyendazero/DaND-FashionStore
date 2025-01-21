package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddToCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class CartItemController {

    private final CartItemService cartItemService;

    @GetMapping("/user/cart-item/cart-items")
    public ResponseEntity<?> cartItems(@RequestParam Long userId){
        APICustomize<List<ItsRctCartResponse>> response = cartItemService.getCartItemByUserid(userId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/user/cart-item")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request){
        APICustomize<String> response = cartItemService.addToCart(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/user/cart-item")
    public ResponseEntity<?> removeFromCart(@RequestBody RemoveFromCartRequest request){
        APICustomize<String> response = cartItemService.removeFromCart(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
