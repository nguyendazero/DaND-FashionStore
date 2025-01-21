package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionProductRequest;

public interface CollectionProductService {

    public APICustomize<Void> add(AddCollectionProductRequest request);

}
