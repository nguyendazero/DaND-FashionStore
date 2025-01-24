package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddReviewRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctReviewResponse;
import com.haibazo_bff_its_rct_webapi.service.ReviewService;
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
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/public/review/reviews")
    public ResponseEntity<?> reviews(@RequestParam Long productId) {
        APICustomize<List<ItsRctReviewResponse>> response = reviewService.reviews(productId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/review")
    public ResponseEntity<?> review(@RequestParam Long id) {
        APICustomize<ItsRctReviewResponse> response = reviewService.review(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/public/review")
    public ResponseEntity<?> create(@RequestParam Long productId, @ModelAttribute AddReviewRequest request, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<ItsRctReviewResponse> response = reviewService.add(productId, request, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/user/review")
    public ResponseEntity<?> delete(@RequestParam Long id, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = reviewService.delete(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
