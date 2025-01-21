package com.haibazo_bff_its_rct_webapi.dto.request;

import com.haibazo_bff_its_rct_webapi.validation.annotation.NoSpecialCharacters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddLanguageRequest {

    @NoSpecialCharacters
    private String code;

    @NoSpecialCharacters
    private String name;

    private MultipartFile image;
}
