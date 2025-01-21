package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCollectionResponse;

import java.util.List;

public interface CollectionService {

    public APICustomize<List<ItsRctCollectionResponse>> collections();

    public APICustomize<ItsRctCollectionResponse> addCollection(AddCollectionRequest request);

    public APICustomize<String> deleteCollection(Long id);

    public APICustomize<ItsRctCollectionResponse> updateCollection(Long id, AddCollectionRequest request);

}
