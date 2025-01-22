package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddDistrictRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDistrictResponse;
import com.haibazo_bff_its_rct_webapi.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping("/public/district")
    public ResponseEntity<?> district(@RequestParam Long id) {
        APICustomize<ItsRctDistrictResponse> response = districtService.district(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/district/districts")
    public ResponseEntity<?> districts(@RequestParam Long cityId) {
        APICustomize<List<ItsRctDistrictResponse>> response = districtService.districts(cityId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/district")
    public ResponseEntity<?> create(@RequestBody AddDistrictRequest request) {
        APICustomize<ItsRctDistrictResponse> response = districtService.create(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/district")
    public ResponseEntity<?> update(@RequestParam Long id, @RequestBody AddDistrictRequest request) {
        APICustomize<ItsRctDistrictResponse> response = districtService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/district")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = districtService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
