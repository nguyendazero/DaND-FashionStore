package com.haibazo_bff_its_rct_webapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItsRctAnswerResponse {
    private Long id;
    private String content;
    private Long questionId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
