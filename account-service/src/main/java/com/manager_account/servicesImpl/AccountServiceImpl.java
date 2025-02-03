package com.manager_account.servicesImpl;

import com.manager_account.dto.request.SignInRequest;
import com.manager_account.dto.request.SignUpRequest;
import com.manager_account.dto.request.UserRequest;
import com.manager_account.dto.response.APICustomize;
import com.manager_account.dto.response.ItsRctUserResponse;
import com.manager_account.dto.response.SignInResponse;
import com.manager_account.entities.Account;
import com.manager_account.enums.ApiError;
import com.manager_account.exceptions.*;
import com.manager_account.repositories.AccountRepository;
import com.manager_account.security.JwtUtils;
import com.manager_account.services.AccountService;
import com.manager_account.utils.TokenUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final WebClient.Builder webClientBuilder; // Khai báo WebClient.Builder
    private WebClient webClient; // Khai báo WebClient

    //Security
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    @PostConstruct // Khởi tạo WebClient khi bean được tạo
    public void init() {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));
    }

    @Override
    public APICustomize<ItsRctUserResponse> signUp(SignUpRequest request) {
        // Kiểm tra xem email đã tồn tại chưa
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Account", "email", request.getEmail());
        }

        // Kiểm tra xem username đã tồn tại chưa
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Account", "username", request.getUsername());
        }

        // Tạo tài khoản mới
        Account newAccount = new Account();
        newAccount.setUsername(request.getUsername());

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        newAccount.setPassword(encodedPassword);

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
        if (userId == null) throw new ResourceNotFoundException("User", "id", "User ID is null");

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
    public APICustomize<SignInResponse> signIn(SignInRequest request) {
        // Tìm kiếm tài khoản bằng username hoặc email
        Account account = accountRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> accountRepository.findByEmail(request.getUsernameOrEmail())
                        .orElseThrow(ErrorSignInException::new));

        // Kiểm tra xem tài khoản có bị block không
        if (!account.isEnabled()) {
            throw new AccountIsBlockException();
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new ErrorSignInException();
        }

        // Xác thực tài khoản
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(account.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Lấy danh sách roles từ authorities
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Tạo JWT token mới
        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);

        // Kiểm tra refresh token
        String currentRefreshToken = account.getRefreshToken();
        LocalDateTime refreshExpiresAt = account.getRefreshExpiresAt();

        // Kiểm tra nếu refresh token là null hoặc đã hết hạn
        if (currentRefreshToken == null || (refreshExpiresAt != null && LocalDateTime.now().isAfter(refreshExpiresAt))) {
            // Tạo refresh token mới
            String refreshToken = jwtUtils.generateRefreshTokenFromUserDetails(userDetails);
            account.setRefreshToken(refreshToken);
            // Cập nhật thời gian hết hạn cho refresh token mới
            account.setRefreshExpiresAt(LocalDateTime.now().plusDays(30)); // 30 ngày
        }

        accountRepository.save(account); // Cập nhật tài khoản

        // Tạo response với đầy đủ các trường cần thiết
        SignInResponse response = new SignInResponse(
                account.getId(),
                account.getUsername(),
                account.getFullName(),
                account.getEmail(),
                roles,
                jwtToken,
                account.getRefreshToken()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    public APICustomize<String> changeRole(Long id, String authorizationHeader) {
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userCurrent = tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token));

        if (!userCurrent.getRole().equals("ROLE_ADMIN") || !userCurrent.getUsername().equals("admin")) {
            throw new ErrorPermissionException();
        }

        Account accountToUpdate = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));

        String newRole = accountToUpdate.getRole().equals("ROLE_ADMIN") ? "ROLE_USER" : "ROLE_ADMIN";
        accountToUpdate.setRole(newRole);
        accountRepository.save(accountToUpdate);

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "New role: " + newRole);
    }

    @Override
    public APICustomize<String> toggleAccountStatus(Long id, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userCurrent = tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token));
        
        if (!userCurrent.getRole().equals("ROLE_ADMIN") || !userCurrent.getUsername().equals("admin")) {
            throw new ErrorPermissionException();
        }
        
        Account accountToUpdate = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));
        
        accountToUpdate.setEnabled(!accountToUpdate.isEnabled());
        accountRepository.save(accountToUpdate);

        String statusMessage = accountToUpdate.isEnabled() ? "unblocked" : "blocked";
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Account has been " + statusMessage);
    }
}