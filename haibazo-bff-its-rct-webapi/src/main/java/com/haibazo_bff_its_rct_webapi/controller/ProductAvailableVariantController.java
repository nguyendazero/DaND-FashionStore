package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductAvailableVariantRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductAvailableVariantResponse;
import com.haibazo_bff_its_rct_webapi.service.ProductAvailableVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class ProductAvailableVariantController {

    private final ProductAvailableVariantService productAvailableVariantService;

    @GetMapping("/public/product-available-variant")
    public ResponseEntity<?> productAvailableVariants(@RequestParam Long productId) {
        APICustomize<List<ItsRctProductAvailableVariantResponse>> response = productAvailableVariantService.productAvailableVariants(productId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }


    @PostMapping("/admin/product-available-variant")
    public ResponseEntity<?> create(@RequestParam Long productId, @ModelAttribute AddProductAvailableVariantRequest request) {
        APICustomize<ItsRctProductAvailableVariantResponse> response = productAvailableVariantService.add(productId, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }


    @GetMapping("/public/product-available-variant/color/{color}/size/{size}/product/{productId}")
    public ResponseEntity<?> getProductAvailableVariants(
            @PathVariable String color,
            @PathVariable String size,
            @PathVariable Long productId) {
        System.out.println("Received request with color: " + color + ", size: " + size + " and productId: " + productId);
        APICustomize<ItsRctProductAvailableVariantResponse> response =
                productAvailableVariantService.findByColorAndSizeAndProductId(color, size, productId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
