package com.manager_account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationInfo {
    private String verificationCode;
    private LocalDateTime sentTime;
    private String username;
    private String fullName;
}
