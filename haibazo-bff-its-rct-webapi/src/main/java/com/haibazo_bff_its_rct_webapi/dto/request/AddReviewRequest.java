package com.haibazo_bff_its_rct_webapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddReviewRequest {
    private String content;
    private BigDecimal stars;
    private List<MultipartFile> images = new ArrayList<>();
}
