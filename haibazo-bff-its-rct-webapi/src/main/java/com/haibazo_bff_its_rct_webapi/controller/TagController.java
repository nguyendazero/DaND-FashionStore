package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddTagRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctTagResponse;
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
public class TagController {

    private final TagService tagService;

    @GetMapping("/public/tag/tags")
    public ResponseEntity<?> tags() {
        APICustomize<List<ItsRctTagResponse>> response = tagService.tags();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/tag")
    public ResponseEntity<?> tag(@RequestParam Long id) {
        APICustomize<ItsRctTagResponse> response = tagService.tag(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/tag")
    public ResponseEntity<?> create(@RequestBody AddTagRequest request) {
        APICustomize<ItsRctTagResponse> response = tagService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/tag")
    public ResponseEntity<?> delete(Long id) {
        APICustomize<String> response = tagService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
