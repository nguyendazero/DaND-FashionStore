package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCollectionResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStyleResponse;
import com.haibazo_bff_its_rct_webapi.service.CategoryService;
import com.haibazo_bff_its_rct_webapi.service.CollectionService;
import com.haibazo_bff_its_rct_webapi.service.ProductService;
import com.haibazo_bff_its_rct_webapi.service.StyleService;
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
    private final CategoryService categoryService;
    private final StyleService styleService;
    private final CollectionService collectionService;

    @GetMapping("/public/home")
    public String home(Model model) {

        APICustomize<List<ItsRctCategoryResponse>> categoryResponse = categoryService.categories();
        APICustomize<List<ItsRctStyleResponse>> stylesResponse = styleService.styles();
        APICustomize<List<ItsRctCollectionResponse>> collectionResponse = collectionService.collections();
        APICustomize<List<ItsRctProductResponse>> productDiscountResponse = productService.getDiscountedProducts();
        APICustomize<List<ItsRctProductResponse>> productResponse = productService.products(
                null, null, null, null, null, null, null, null, null, null, 0, 10);
        
        model.addAttribute("products", productResponse.getResult());
        model.addAttribute("productsDiscount", productDiscountResponse.getResult());
        model.addAttribute("multiplier", BigDecimal.valueOf(2));
        model.addAttribute("categories", categoryResponse.getResult());
        model.addAttribute("styles", stylesResponse.getResult());
        model.addAttribute("collections", collectionResponse.getResult());

        return "index";
    }
    
}
