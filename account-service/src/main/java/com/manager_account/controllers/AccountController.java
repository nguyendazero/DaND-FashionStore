package com.manager_account.controllers;

import com.manager_account.dto.request.SignInRequest;
import com.manager_account.dto.request.SignUpRequest;
import com.manager_account.dto.response.APICustomize;
import com.manager_account.dto.response.ItsRctUserResponse;
import com.manager_account.entities.Account;
import com.manager_account.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/its-rct/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/user/account/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/public/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest accountRequest) {
        APICustomize<ItsRctUserResponse> response = accountService.signUp(accountRequest);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest request) {
        APICustomize<ItsRctUserResponse> response = accountService.signIn(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}