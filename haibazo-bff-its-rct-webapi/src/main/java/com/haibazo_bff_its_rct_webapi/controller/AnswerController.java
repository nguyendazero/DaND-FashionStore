package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAnswerRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAnswerResponse;
import com.haibazo_bff_its_rct_webapi.service.AnswerService;
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
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/admin/answer")
    public ResponseEntity<?> create(@RequestParam Long questionId, @ModelAttribute AddAnswerRequest request, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<ItsRctAnswerResponse> response = answerService.add(questionId, request, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/answer/answers")
    public ResponseEntity<?> answers(@RequestParam Long questionId) {
        APICustomize<List<ItsRctAnswerResponse>> response = answerService.getByQuestionId(questionId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/answer")
    public ResponseEntity<?> answer(@RequestParam Long id) {
        APICustomize<ItsRctAnswerResponse> response = answerService.getById(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/answer")
    public ResponseEntity<?> delete(@RequestParam Long id, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = answerService.delete(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }
    
}
