package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddQuestionRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddReviewRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctQuestionResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctReviewResponse;
import com.haibazo_bff_its_rct_webapi.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/public/question")
    public ResponseEntity<?> create(@RequestParam Long productId, @ModelAttribute AddQuestionRequest request) {
        APICustomize<ItsRctQuestionResponse> response = questionService.add(productId, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/question/questions")
    public ResponseEntity<?> questions(@RequestParam Long productId) {
        APICustomize<List<ItsRctQuestionResponse>> response = questionService.questionsByProduct(productId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/question")
    public ResponseEntity<?> question(@RequestParam Long id) {
        APICustomize<ItsRctQuestionResponse> response = questionService.getById(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/question")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = questionService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
