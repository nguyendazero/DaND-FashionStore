package com.haibazo_bff_its_rct_webapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostsRequest {
    private String search;
    private String orderBy;
    private String sortBy;
    private List<Long> categoryIds = new ArrayList<>();
}
