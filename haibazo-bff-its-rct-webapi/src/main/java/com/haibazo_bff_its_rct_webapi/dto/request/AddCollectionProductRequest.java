package com.haibazo_bff_its_rct_webapi.dto.request;

import com.haibazo_bff_its_rct_webapi.model.Collection;
import com.haibazo_bff_its_rct_webapi.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCollectionProductRequest {
    private Long collectionId;
    private Long productId;
}
