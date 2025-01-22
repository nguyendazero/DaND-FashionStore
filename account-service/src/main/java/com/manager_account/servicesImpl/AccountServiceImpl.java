package com.manager_account.servicesImpl;

import com.manager_account.dto.request.SignInRequest;
import com.manager_account.dto.request.SignUpRequest;
import com.manager_account.dto.request.UserRequest;
import com.manager_account.dto.response.APICustomize;
import com.manager_account.dto.response.ItsRctUserResponse;
import com.manager_account.entities.Account;
import com.manager_account.enums.ApiError;
import com.manager_account.repositories.AccountRepository;
import com.manager_account.services.AccountService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final WebClient.Builder webClientBuilder; // Khai báo WebClient.Builder
    private WebClient webClient; // Khai báo WebClient

    @PostConstruct // Khởi tạo WebClient khi bean được tạo
    public void init() {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public APICustomize<ItsRctUserResponse> signUp(SignUpRequest request) {

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Tạo tài khoản mới
        Account newAccount = new Account();
        newAccount.setUsername(request.getUsername());
        newAccount.setPassword(request.getPassword());
        newAccount.setFullName(request.getName());
        newAccount.setEmail(request.getEmail());
        newAccount.setEnabled(true);
        newAccount.setRole("ROLE_USER");
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setUpdatedAt(LocalDateTime.now());

        // Lưu tài khoản
        Account savedAccount = accountRepository.save(newAccount);
        savedAccount.setHaibazoAccountId(savedAccount.getId());
        accountRepository.save(savedAccount);

        // Tạo đối tượng User và gửi yêu cầu tạo người dùng
        UserRequest user = new UserRequest(savedAccount.getHaibazoAccountId());
        Long userId = webClient.post()
                .uri("/api/bff/its-rct/v1/ecommerce/public/user/create")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(Long.class)
                .block();

        // Kiểm tra xem userId có null không
        if (userId == null) {
            throw new RuntimeException("Error creating user, userId is null");
        }

        ItsRctUserResponse userResponse = new ItsRctUserResponse(
                userId,
                savedAccount.getUsername(),
                savedAccount.getEmail(),
                savedAccount.getFullName(),
                savedAccount.getHaibazoAccountId(),
                savedAccount.isEnabled(),
                savedAccount.getRole(),
                savedAccount.getStatus(),
                savedAccount.getAvatar(),
                savedAccount.getCreatedAt(),
                savedAccount.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), userResponse);
    }

    @Override
    public APICustomize<ItsRctUserResponse> signIn(SignInRequest request) {

        Account account = accountRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> accountRepository.findByEmail(request.getUsernameOrEmail())
                        .orElseThrow(() -> new RuntimeException("Invalid username or email")));

        // Kiểm tra xem mật khẩu có khớp không
        if (!request.getPassword().equals(account.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        ItsRctUserResponse accountResponse = new ItsRctUserResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getFullName(),
                account.getHaibazoAccountId(),
                account.isEnabled(),
                account.getRole(),
                account.getStatus(),
                account.getAvatar(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), accountResponse);
    }
}