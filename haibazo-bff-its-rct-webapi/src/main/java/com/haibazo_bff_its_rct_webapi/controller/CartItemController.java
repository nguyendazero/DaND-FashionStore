package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCartResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.exception.*;
import com.haibazo_bff_its_rct_webapi.service.CartItemService;
import com.haibazo_bff_its_rct_webapi.service.CouponService;
import com.haibazo_bff_its_rct_webapi.utils.CookieUtil;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
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
    private final TokenUtil tokenUtil;
    

    @GetMapping("/public/cart-item/cart-items")
    public String cartItems(Model model, HttpServletRequest request){
        
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        if (jwtToken == null) throw new UnauthorizedException();
        model.addAttribute("jwtToken", jwtToken);
        
        APICustomize<List<ItsRctCartResponse>> response = cartItemService.getCartItems(jwtToken);
        BigDecimal totalPrice = cartItemService.calculateTotalPrice(jwtToken);
        BigDecimal discountAmount = (BigDecimal) model.getAttribute("discountAmount");
        model.addAttribute("discountAmount", discountAmount);
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

    @GetMapping("/public/cart-item/{variantId}")
    public RedirectView removeFromCart(@PathVariable Long variantId, HttpServletRequest httpRequest) {
        // Lấy cookie từ request
        String jwtToken = CookieUtil.getJwtTokenFromCookies(httpRequest);
        if (jwtToken == null) throw new UnauthorizedException();

        // Gọi service để xóa sản phẩm khỏi giỏ hàng
        APICustomize<String> response = cartItemService.removeFromCart(variantId, "Bearer " + jwtToken);

        // Trả về phản hồi
        return new RedirectView("http://localhost:8386/api/bff/its-rct/v1/ecommerce/public/home");
    }

    @PostMapping("/public/cart-item/plus/{variantId}")
    public ResponseEntity<?> increaseQuantity(@PathVariable Long variantId, HttpServletRequest httpRequest) {
        String jwtToken = CookieUtil.getJwtTokenFromCookies(httpRequest);
        if (jwtToken == null) throw new UnauthorizedException();

        APICustomize<String> response = cartItemService.changeQuantity(variantId, "Bearer " + jwtToken, 1);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/cart-item/minus/{variantId}")
    public ResponseEntity<?> decreaseQuantity(@PathVariable Long variantId, HttpServletRequest httpRequest) {
        String jwtToken = CookieUtil.getJwtTokenFromCookies(httpRequest);
        if (jwtToken == null) throw new UnauthorizedException();

        APICustomize<String> response = cartItemService.changeQuantity(variantId, "Bearer " + jwtToken, -1);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/cart-item/apply-coupon")
    public String applyCoupon(@RequestParam String code, HttpServletRequest request, Model model) {
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        if (jwtToken == null) throw new UnauthorizedException();

        // Lấy thông tin người dùng từ token
        ItsRctUserResponse userResponse = tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(jwtToken));

        // Gọi service để áp dụng coupon
        cartItemService.applyCoupon(code, userResponse, model);

        // Gọi phương thức cartItems để trả về trang giỏ hàng
        return cartItems(model, request);
    }

}
