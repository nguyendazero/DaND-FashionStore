package com.manager_account.services;

import com.manager_account.dto.request.SignInRequest;
import com.manager_account.dto.request.SignUpRequest;
import com.manager_account.dto.response.APICustomize;
import com.manager_account.dto.response.ItsRctUserResponse;
import com.manager_account.entities.Account;

public interface AccountService {

    public Account getAccountById(Long id);

    public APICustomize<ItsRctUserResponse> signUp(SignUpRequest request);

    public APICustomize<ItsRctUserResponse> signIn(SignInRequest request);

}
