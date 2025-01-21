package com.manager_account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItsRctUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Long haibazoAuthAlias;
    private boolean enabled;
    private String role;
    private String status;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}