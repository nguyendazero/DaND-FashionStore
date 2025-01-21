package com.haibazo_bff_its_rct_webapi.dto.response;

import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItsRctPostCommentResponse {
    private Long id;
    private String content;
    private User user;
    private UserTemp userTemp;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
