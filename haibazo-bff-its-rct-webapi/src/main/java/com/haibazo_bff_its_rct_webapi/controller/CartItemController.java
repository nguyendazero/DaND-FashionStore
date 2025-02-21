package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddToCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.RemoveFromCartRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.exception.UnauthorizedException;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
import com.haibazo_bff_its_rct_webapi.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class CartItemController {

    private final CartItemService cartItemService;

    @GetMapping("/public/cart-item/cart-items")
    public String cartItems(Model model, HttpServletRequest request){
        
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        if (jwtToken == null) throw new UnauthorizedException();
        model.addAttribute("jwtToken", jwtToken);
        
        APICustomize<List<ItsRctCartResponse>> response = cartItemService.getCartItems(jwtToken);
        BigDecimal totalPrice = cartItemService.calculateTotalPrice(jwtToken);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("items", response.getResult());
        return "cart";
    }


    @PostMapping("/public/cart-item/{variantId}")
    public RedirectView addToCart(Model model, @PathVariable Long variantId, HttpServletRequest request) {
        
        // Lấy cookie từ request
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        if (jwtToken == null) throw new UnauthorizedException();
        model.addAttribute("jwtToken", jwtToken);
        
        cartItemService.addToCart(variantId, "Bearer " + jwtToken);
        
        return new RedirectView("http://localhost:8386/api/bff/its-rct/v1/ecommerce/public/home");
    }

    @DeleteMapping("/user/cart-item")
    public ResponseEntity<?> removeFromCart(@RequestBody RemoveFromCartRequest request, HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = cartItemService.removeFromCart(request, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
