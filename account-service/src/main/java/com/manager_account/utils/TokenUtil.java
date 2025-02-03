package com.manager_account.utils;

import com.manager_account.dto.response.ItsRctUserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TokenUtil {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    @PostConstruct // Khởi tạo WebClient khi bean được tạo
    public void init() {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    // Phương thức để lấy token từ header
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    // Phương thức để giải mã token và lấy haibazoAccountId
    public Long getHaibazoAccountIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret) // Sử dụng secret key của bạn
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Trả về haibazoAccountId từ claims
        return Long.valueOf(claims.get("haibazoAccountId").toString());
    }

    // Phương thức để gọi account-service và lấy thông tin tài khoản
    public ItsRctUserResponse getUserByHaibazoAccountId(Long haibazoAccountId) {
        return webClient.get()
                .uri("/api/bff/its-rct/v1/account/user/account/{id}", haibazoAccountId)
                .retrieve()
                .bodyToMono(ItsRctUserResponse.class)
                .block();
    }
}
