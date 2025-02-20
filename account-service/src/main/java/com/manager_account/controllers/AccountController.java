package com.manager_account.controllers;

import com.manager_account.dto.request.SignInRequest;
import com.manager_account.dto.request.SignUpRequest;
import com.manager_account.dto.request.UpdateInfoRequest;
import com.manager_account.dto.response.APICustomize;
import com.manager_account.dto.response.ItsRctUserResponse;
import com.manager_account.dto.response.SignInResponse;
import com.manager_account.entities.Account;
import com.manager_account.exceptions.GlobalExceptionHandler;
import com.manager_account.security.AccessTokenResponse;
import com.manager_account.security.JwtUtils;
import com.manager_account.services.AccountService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
@RequestMapping("/api/bff/its-rct/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JwtUtils jwtUtils;
    private final GlobalExceptionHandler globalExceptionHandler;

    @GetMapping("/user/account/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/public/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest accountRequest) {
        APICustomize<String> response = accountService.signUp(accountRequest);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String code) {
        APICustomize<ItsRctUserResponse> response = accountService.verifyEmail(email, code);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/login-page")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/public/sign-in")
    public RedirectView signIn(@ModelAttribute SignInRequest request, HttpServletResponse response) {
        APICustomize<SignInResponse> apiResponse = accountService.signIn(request);
        String jwtToken = apiResponse.getResult().getJwtToken();

        // Tạo cookie
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);

        // Thêm cookie vào response
        response.addCookie(cookie);
        
        String redirectUrl = "http://localhost:8386/api/bff/its-rct/v1/ecommerce/public/home";
        return new RedirectView(redirectUrl);
    }


    @GetMapping("public/logout")
    public RedirectView logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Đặt thời gian sống bằng 0 để xóa cookie

        // Thêm cookie vào response
        response.addCookie(cookie);
        
        String redirectUrl = "http://localhost:8386/api/bff/its-rct/v1/ecommerce/public/home";
        return new RedirectView(redirectUrl);
    }

    @PostMapping("/public/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String newAccessToken = jwtUtils.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));
    }

    @PutMapping("/admin/account/change-role")
    public ResponseEntity<?> changeRole(@RequestParam Long id, HttpServletRequest httpRequest) {
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = accountService.changeRole(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/account/toggle-account-status")
    public ResponseEntity<?> toggleAccountStatus(@RequestParam Long id, HttpServletRequest httpRequest) {
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = accountService.toggleAccountStatus(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/user/account/update/{haibazoAccountId}")
    public ResponseEntity<?> updateAccount(
            @PathVariable Long haibazoAccountId,
            @RequestBody UpdateInfoRequest request) {
        APICustomize<String> response = accountService.updateAccount(haibazoAccountId, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/language/choose-language")
    public ResponseEntity<?> chooseLanguage(@RequestParam String code) {
        String fileName = switch (code) {
            case "vn" -> "messages_vn_VN.properties";
            case "us" -> "messages_en_US.properties";
            default -> null;
        };

        if (fileName == null) return ResponseEntity.badRequest().body("Invalid language code: " + code);

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) throw new IOException("File not found: " + fileName);
            globalExceptionHandler.loadMessages(input);
            return ResponseEntity.ok("Language set to: " + code);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}