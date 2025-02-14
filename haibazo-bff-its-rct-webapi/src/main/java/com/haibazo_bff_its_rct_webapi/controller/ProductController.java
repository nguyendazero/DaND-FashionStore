package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStyleResponse;
import com.haibazo_bff_its_rct_webapi.enums.Collections;
import com.haibazo_bff_its_rct_webapi.exception.ListProductEmptyException;
import com.haibazo_bff_its_rct_webapi.service.CategoryService;
import com.haibazo_bff_its_rct_webapi.service.ProductService;
import com.haibazo_bff_its_rct_webapi.service.StyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final StyleService styleService;

    @GetMapping("/public/product/products")
    public String products(
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Collections collection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean discount,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize, Model model) {

        // Lấy danh sách danh mục và kiểu
        APICustomize<List<ItsRctCategoryResponse>> categoryResponse = categoryService.categories();
        APICustomize<List<ItsRctStyleResponse>> stylesResponse = styleService.styles();
        APICustomize<List<ItsRctProductResponse>> productsResponse = productService.products(
                size, color, minPrice, maxPrice, style, category, collection, name, discount, sortBy, sortOrder, pageIndex, pageSize);
        
        // Cập nhật model cho danh mục và kiểu
        model.addAttribute("categories", categoryResponse.getResult());
        model.addAttribute("styles", stylesResponse.getResult());
        model.addAttribute("products", productsResponse.getResult());
        return "products";
    }

    @GetMapping("/public/product")
    public ResponseEntity<?> product(@RequestParam Long id){
        APICustomize<ItsRctProductResponse> response = productService.getProductById(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/product")
    public ResponseEntity<?> create(@ModelAttribute AddProductRequest request){
        APICustomize<ItsRctProductResponse> response = productService.addProduct(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/product")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = productService.deleteProduct(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/product")
    public ResponseEntity<?> update(@RequestParam Long id, @ModelAttribute AddProductRequest request) {
        APICustomize<ItsRctProductResponse> response = productService.updateProduct(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
