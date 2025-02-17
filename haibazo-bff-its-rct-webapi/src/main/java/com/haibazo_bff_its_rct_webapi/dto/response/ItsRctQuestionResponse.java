package com.haibazo_bff_its_rct_webapi.dto.response;

import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctQuestionResponse {
    private Long id;
    private ItsRctUserResponse user;
    private UserTemp userTemp;
    private Long productId;
    private String content;
    private List<ItsRctImageResponse> images;
    private List<ItsRctAnswerResponse> answers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
