package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostCommentResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctTagResponse;
import com.haibazo_bff_its_rct_webapi.service.PostCommentService;
import com.haibazo_bff_its_rct_webapi.service.PostService;
import com.haibazo_bff_its_rct_webapi.service.PostTagService;
import com.haibazo_bff_its_rct_webapi.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class PostController {

    private final PostService postService;
    private final PostCommentService postCommentService;
    private final PostTagService postTagService;

    @GetMapping("/public/post/posts")
    public String posts(Model model) {
        
        APICustomize<List<ItsRctPostResponse>> postResponse = postService.posts();
        model.addAttribute("posts", postResponse.getResult());
        
        return "posts";
    }

    @GetMapping("/public/post/{id}")
    public String post(@PathVariable Long id, Model model, HttpServletRequest request) {
        // Lấy cookie từ request
        String jwtToken = CookieUtil.getJwtTokenFromCookies(request);
        model.addAttribute("jwtToken", jwtToken);
        
        APICustomize<ItsRctPostResponse> response = postService.post(id);
        APICustomize<List<ItsRctPostCommentResponse>> postCommentsResponse = postCommentService.getByPostId(id);
        APICustomize<List<ItsRctTagResponse>> tags = postService.getTagsByPostId(id);
        model.addAttribute("post", response.getResult());
        model.addAttribute("postComments", postCommentsResponse.getResult());
        model.addAttribute("tags", tags.getResult());
        
        return "post-detail";
    }

    @PostMapping("/admin/post")
    public ResponseEntity<?> create(@ModelAttribute AddPostRequest request) {
        APICustomize<ItsRctPostResponse> response = postService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/post")
    public ResponseEntity<?> delete(Long id) {
        APICustomize<String> response = postService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
