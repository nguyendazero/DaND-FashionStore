package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddProductRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctProductResponse;
import com.haibazo_bff_its_rct_webapi.enums.Collections;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    public APICustomize<List<ItsRctProductResponse>> products(String size, String color, BigDecimal minPrice, BigDecimal maxPrice, String style, String category, Collections collection, String name, String sortBy, String sortOrder, int pageIndex, int pageSize);

    public APICustomize<ItsRctProductResponse> getProductById(Long id);

    public APICustomize<ItsRctProductResponse> addProduct(AddProductRequest request);

    public APICustomize<String> deleteProduct(Long productId);

    public APICustomize<ItsRctProductResponse> updateProduct(Long productId, AddProductRequest request);
}
