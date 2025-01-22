package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddStyleRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctStyleResponse;
import com.haibazo_bff_its_rct_webapi.service.StyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class StyleController {

    private final StyleService styleService;

    @GetMapping("/public/style/styles")
    public ResponseEntity<?> styles(){
        APICustomize<List<ItsRctStyleResponse>> response = styleService.styles();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/style")
    public ResponseEntity<?> create(@ModelAttribute AddStyleRequest request){
        APICustomize<ItsRctStyleResponse> response = styleService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/style")
    public ResponseEntity<?> delete(@RequestParam Long id){
        APICustomize<String> response = styleService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/style")
    public ResponseEntity<?> update(@RequestParam Long id, @ModelAttribute AddStyleRequest request) {
        APICustomize<ItsRctStyleResponse> response = styleService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
