package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.enums.Collections;
import com.haibazo_bff_its_rct_webapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/public/product/products")
    public ResponseEntity<APICustomize<List<ItsRctProductResponse>>> products(
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Collections collection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize) {

        APICustomize<List<ItsRctProductResponse>> response = productService.products(
                size, color, minPrice, maxPrice, style, category, collection, name, sortBy, sortOrder, pageIndex, pageSize);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
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
