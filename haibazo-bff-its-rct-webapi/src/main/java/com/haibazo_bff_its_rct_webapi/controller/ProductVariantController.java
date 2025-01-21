package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductVariantRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctLanguageResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductVariantResponse;
import com.haibazo_bff_its_rct_webapi.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @GetMapping("/public/product-variant")
    public ResponseEntity<?> productVariants(@RequestParam Long productAvailableVariantId) {
        APICustomize<List<ItsRctProductVariantResponse>> response = productVariantService.productVariants(productAvailableVariantId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/product-variant")
    public ResponseEntity<?> create(@RequestParam Long productAvailableVariantId, @RequestBody AddProductVariantRequest request) {
        APICustomize<ItsRctProductVariantResponse> response = productVariantService.addProductVariant(productAvailableVariantId, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
