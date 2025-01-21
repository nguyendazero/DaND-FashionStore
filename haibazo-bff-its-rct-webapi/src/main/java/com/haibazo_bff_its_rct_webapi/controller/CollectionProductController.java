package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionProductRequest;
import com.haibazo_bff_its_rct_webapi.service.CollectionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class CollectionProductController {

    private final CollectionProductService collectionProductService;

    @PostMapping("/admin/collection-product")
    public ResponseEntity<?> create(@RequestBody AddCollectionProductRequest request) {
        collectionProductService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
