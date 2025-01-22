package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionProductRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostTagRequest;
import com.haibazo_bff_its_rct_webapi.service.CollectionProductService;
import com.haibazo_bff_its_rct_webapi.service.PostTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class PostTagController {

    private final PostTagService postTagService;

    @PostMapping("/admin/post-tag")
    public ResponseEntity<?> create(@RequestBody AddPostTagRequest request) {
        postTagService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
