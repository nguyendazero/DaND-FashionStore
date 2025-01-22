package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCategoryRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCollectionResponse;
import com.haibazo_bff_its_rct_webapi.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping("/public/collection/collections")
    public ResponseEntity<?> collections(){
        APICustomize<List<ItsRctCollectionResponse>> response = collectionService.collections();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/collection")
    public ResponseEntity<?> add(@ModelAttribute AddCollectionRequest request){
        APICustomize<ItsRctCollectionResponse> response = collectionService.addCollection(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/collection")
    public ResponseEntity<?> delete(@RequestParam Long id){
        APICustomize<String> response = collectionService.deleteCollection(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/collection")
    public ResponseEntity<?> update(@RequestParam Long id, @ModelAttribute AddCollectionRequest request) {
        APICustomize<ItsRctCollectionResponse> response = collectionService.updateCollection(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
