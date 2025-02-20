package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.*;
import com.haibazo_bff_its_rct_webapi.service.*;
import com.haibazo_bff_its_rct_webapi.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class HomeController {

    private final ProductService productService;
    private final PostService postService;

    @GetMapping("/public/home")
    public String home(Model model, HttpServletRequest request) {
        // Lấy cookie từ request
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        model.addAttribute("jwtToken", jwtToken);

        // Lấy dữ liệu từ các service
        APICustomize<List<ItsRctPostResponse>> postResponse = postService.posts();
        APICustomize<List<ItsRctProductResponse>> productDiscountResponse = productService.getDiscountedProducts();
        APICustomize<List<ItsRctProductResponse>> productResponse = productService.products(
                null, null, null, null, null, null, null, null, null, null, null, 0, 10);

        // Thêm dữ liệu vào model
        model.addAttribute("posts", postResponse.getResult());
        model.addAttribute("products", productResponse.getResult());
        model.addAttribute("productsDiscount", productDiscountResponse.getResult());
        model.addAttribute("multiplier", BigDecimal.valueOf(2));

        return "index";
    }
    
}
