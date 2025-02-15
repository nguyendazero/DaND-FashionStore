package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostCommentRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostCommentResponse;
import com.haibazo_bff_its_rct_webapi.service.PostCommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping("/public/post-comment")
    public RedirectView create(@RequestParam Long postId, @ModelAttribute AddPostCommentRequest request, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<ItsRctPostCommentResponse> response = postCommentService.add(postId, request, authorizationHeader);
        
        String redirectUrl = "http://localhost:8386/api/bff/its-rct/v1/ecommerce/public/post/" + postId;
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/public/post-comment/post-comments")
    public ResponseEntity<?> postComments(@RequestParam Long postId) {
        APICustomize<List<ItsRctPostCommentResponse>> response = postCommentService.getByPostId(postId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/post-comment")
    public ResponseEntity<?> postComment(@RequestParam Long id) {
        APICustomize<ItsRctPostCommentResponse> response = postCommentService.postComment(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/user/post-comment")
    public ResponseEntity<?> delete(@RequestParam Long id, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<String> response = postCommentService.delete(id, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }
}
