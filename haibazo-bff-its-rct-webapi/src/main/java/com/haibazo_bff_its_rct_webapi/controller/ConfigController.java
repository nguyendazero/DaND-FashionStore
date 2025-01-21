package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddConfigRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctConfigResponse;
import com.haibazo_bff_its_rct_webapi.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/public/config/configs")
    public ResponseEntity<?> configs() {
        APICustomize<List<ItsRctConfigResponse>> response = configService.configs();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/config")
    public ResponseEntity<?> config(@RequestParam Long id) {
        APICustomize<ItsRctConfigResponse> response = configService.config(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/config")
    public ResponseEntity<?> create(@RequestBody AddConfigRequest request) {
        APICustomize<ItsRctConfigResponse> response = configService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/config")
    public ResponseEntity<?> create(@RequestParam Long id) {
        APICustomize<String> response = configService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/config")
    public ResponseEntity<?> create(@RequestParam Long id, @RequestBody AddConfigRequest request) {
        APICustomize<ItsRctConfigResponse> response = configService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
