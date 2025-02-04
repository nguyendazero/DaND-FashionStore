package com.manager_account.services;

import com.manager_account.dto.request.SignInRequest;
import com.manager_account.dto.request.SignUpRequest;
import com.manager_account.dto.request.UpdateInfoRequest;
import com.manager_account.dto.response.APICustomize;
import com.manager_account.dto.response.ItsRctUserResponse;
import com.manager_account.dto.response.SignInResponse;
import com.manager_account.entities.Account;

public interface AccountService {

    public Account getAccountById(Long id);

    public APICustomize<String> signUp(SignUpRequest request);

    public APICustomize<ItsRctUserResponse> verifyEmail(String email, String code);

    public APICustomize<SignInResponse> signIn(SignInRequest request);

    public APICustomize<String> changeRole(Long id, String authorizationHeader);

    public APICustomize<String> toggleAccountStatus(Long id, String authorizationHeader);

    public APICustomize<String> updateAccount(Long haibazoAccountId, UpdateInfoRequest request);

}
