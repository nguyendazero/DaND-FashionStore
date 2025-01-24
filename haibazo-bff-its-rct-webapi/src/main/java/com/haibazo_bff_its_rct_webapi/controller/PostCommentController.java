package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostCommentRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostCommentResponse;
import com.haibazo_bff_its_rct_webapi.service.PostCommentService;
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
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping("/public/post-comment")
    public ResponseEntity<?> create(@RequestParam Long postId, @RequestBody AddPostCommentRequest request, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<ItsRctPostCommentResponse> response = postCommentService.add(postId, request, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
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
