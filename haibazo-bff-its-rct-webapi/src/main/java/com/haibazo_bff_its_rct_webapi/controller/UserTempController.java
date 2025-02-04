package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.UserTempResponse;
import com.haibazo_bff_its_rct_webapi.service.UserTempService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class UserTempController {
    
    private final UserTempService userTempService;

    @GetMapping("/admin/guest/guests")
    public ResponseEntity<?> guests(HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<List<UserTempResponse>> response = userTempService.guests(authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/admin/guest")
    public ResponseEntity<?> guest(@RequestParam Long id, HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<UserTempResponse> response = userTempService.guest(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/guest")
    public ResponseEntity<?> delete(@RequestParam Long id, HttpServletRequest httpRequest){
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = userTempService.delete(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }
    
    
    
}
