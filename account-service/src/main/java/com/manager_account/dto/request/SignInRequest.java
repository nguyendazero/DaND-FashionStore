package com.manager_account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.manager_account.entities.Account;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    private String usernameOrEmail;
    private String password;
}
