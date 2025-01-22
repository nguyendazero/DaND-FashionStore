package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddTagRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctTagResponse;
import com.haibazo_bff_its_rct_webapi.service.PostService;
import com.haibazo_bff_its_rct_webapi.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class PostController {

    private final PostService postService;

    @GetMapping("/public/post/posts")
    public ResponseEntity<?> posts() {
        APICustomize<List<ItsRctPostResponse>> response = postService.posts();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/post")
    public ResponseEntity<?> post(@RequestParam Long id) {
        APICustomize<ItsRctPostResponse> response = postService.post(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/post")
    public ResponseEntity<?> create(@RequestBody AddPostRequest request) {
        APICustomize<ItsRctPostResponse> response = postService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/post")
    public ResponseEntity<?> delete(Long id) {
        APICustomize<String> response = postService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
