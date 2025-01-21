package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddWardRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctWardResponse;
import com.haibazo_bff_its_rct_webapi.service.WardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class WardController {

    private final WardService wardService;

    @GetMapping("/public/ward/wards")
    public ResponseEntity<?> wards(@RequestParam Long districtId) {
        APICustomize<List<ItsRctWardResponse>> response = wardService.wards(districtId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/ward")
    public ResponseEntity<?> ward(@RequestParam Long id) {
        APICustomize<ItsRctWardResponse> response = wardService.ward(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/ward")
    public ResponseEntity<?> create(@RequestBody AddWardRequest request) {
        APICustomize<ItsRctWardResponse> response = wardService.create(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/ward")
    public ResponseEntity<?> update(@RequestParam Long id, @RequestBody AddWardRequest request) {
        APICustomize<ItsRctWardResponse> response = wardService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/ward")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = wardService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
