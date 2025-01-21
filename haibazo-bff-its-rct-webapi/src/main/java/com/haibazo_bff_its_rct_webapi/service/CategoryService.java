package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCategoryRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCategoryResponse;

import java.util.List;

public interface CategoryService {

    public APICustomize<List<ItsRctCategoryResponse>> categories();

    public APICustomize<ItsRctCategoryResponse> category(Long categoryId);

    public APICustomize<ItsRctCategoryResponse> addCategory(AddCategoryRequest request);

    public APICustomize<String> deleteCategory(Long id);

    public APICustomize<ItsRctCategoryResponse> updateCategory(Long id, AddCategoryRequest request);

}
