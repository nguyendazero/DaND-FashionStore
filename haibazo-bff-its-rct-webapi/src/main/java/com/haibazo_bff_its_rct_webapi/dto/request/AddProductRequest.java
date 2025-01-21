package com.haibazo_bff_its_rct_webapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductRequest {
    private String name;
    private String weigh;
    private String description;
    private String introduction;
    private MultipartFile highLightedImageUrl;
    private Long categoryId;
    private Long styleId;
    private List<MultipartFile> images;
    // có thể null
    private LocalDateTime dateEndSale;
    private Long discountId;
}
