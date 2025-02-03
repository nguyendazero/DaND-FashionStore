package com.manager_account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInfoRequest {
    private String avatar;
    private String fullName;
    private String status;
}

