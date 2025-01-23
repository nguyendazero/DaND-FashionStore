package com.manager_account.controllers;

import com.manager_account.dto.request.SignInRequest;
import com.manager_account.dto.request.SignUpRequest;
import com.manager_account.dto.response.APICustomize;
import com.manager_account.dto.response.ItsRctUserResponse;
import com.manager_account.dto.response.SignInResponse;
import com.manager_account.entities.Account;
import com.manager_account.security.AccessTokenResponse;
import com.manager_account.security.JwtUtils;
import com.manager_account.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bff/its-rct/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JwtUtils jwtUtils;

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
        APICustomize<SignInResponse> response = accountService.signIn(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String newAccessToken = jwtUtils.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));
    }

}