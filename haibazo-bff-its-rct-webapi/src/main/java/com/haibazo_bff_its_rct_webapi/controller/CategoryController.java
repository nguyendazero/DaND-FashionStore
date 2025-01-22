package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCategoryRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;
import com.haibazo_bff_its_rct_webapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/public/category/categories")
    public ResponseEntity<?> categories(){
        APICustomize<List<ItsRctCategoryResponse>> response = categoryService.categories();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<?> addCategory(@ModelAttribute AddCategoryRequest request){
        APICustomize<ItsRctCategoryResponse> response = categoryService.addCategory(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/category")
    public ResponseEntity<?> deleteCategory(@RequestParam Long id){
        APICustomize<String> response = categoryService.deleteCategory(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/category")
    public ResponseEntity<?> updateCategory(@RequestParam Long id, @ModelAttribute AddCategoryRequest request) {
        APICustomize<ItsRctCategoryResponse> response = categoryService.updateCategory(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
