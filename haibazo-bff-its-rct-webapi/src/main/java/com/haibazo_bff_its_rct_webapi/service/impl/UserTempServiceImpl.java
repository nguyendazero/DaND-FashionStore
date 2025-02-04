package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.UserTempResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ErrorPermissionException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.UserTemp;
import com.haibazo_bff_its_rct_webapi.repository.UserTempRepository;
import com.haibazo_bff_its_rct_webapi.service.UserTempService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTempServiceImpl implements UserTempService {
    
    private final UserTempRepository userTempRepository;
    private final TokenUtil tokenUtil;
    
    @Override
    public APICustomize<List<UserTempResponse>> guests(String authorizationHeader) {
        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userCurrent = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;
        
        // Kiểm tra quyền hạn
        if (userCurrent == null || !userCurrent.getRole().contains("ROLE_ADMIN") || !userCurrent.getUsername().equals("admin")) {
            throw new ErrorPermissionException();
        }

        List<UserTemp> users = userTempRepository.findAll();
        List<UserTempResponse> userResponses = users.stream()
                .map(user -> new UserTempResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getAvatar(),
                        user.getCreatedAt()
                ))
                .toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), userResponses);
    }

    @Override
    public APICustomize<UserTempResponse> guest(Long id, String authorizationHeader) {

        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userCurrent = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra quyền hạn
        if (userCurrent == null || !userCurrent.getRole().contains("ROLE_ADMIN") || !userCurrent.getUsername().equals("admin")) {
            throw new ErrorPermissionException();
        }

        UserTemp userTemp = userTempRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", "id", id.toString()));
        
        UserTempResponse userResponse = new UserTempResponse(
               userTemp.getId(),
               userTemp.getFullName(),
               userTemp.getEmail(),
               userTemp.getAvatar(),
               userTemp.getCreatedAt() 
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), userResponse);
    }

    @Override
    public APICustomize<String> delete(Long id, String authorizationHeader) {

        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userCurrent = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra quyền hạn
        if (userCurrent == null || !userCurrent.getRole().contains("ROLE_ADMIN") || !userCurrent.getUsername().equals("admin")) {
            throw new ErrorPermissionException();
        }

        UserTemp userTemp = userTempRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", "id", id.toString()));
        
        userTempRepository.delete(userTemp);
        
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Successfully deleted guest with id = " + id);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldUserTemps() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<UserTemp> oldUsers = userTempRepository.findAllByCreatedAtBefore(thirtyDaysAgo);
        userTempRepository.deleteAll(oldUsers);
    }
}
