package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctImageResponse;
import com.haibazo_bff_its_rct_webapi.enums.EntityType;
import com.haibazo_bff_its_rct_webapi.model.Image;

import java.util.List;

public interface ImageService {

    public APICustomize<List<ItsRctImageResponse>> getImages(Long entityId, EntityType entityType);

}
