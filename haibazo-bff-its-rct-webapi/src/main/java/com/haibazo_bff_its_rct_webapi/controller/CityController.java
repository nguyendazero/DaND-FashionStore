package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCityRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCityResponse;
import com.haibazo_bff_its_rct_webapi.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class CityController {

    private final CityService cityService;

    @GetMapping("/public/city/cities")
    public ResponseEntity<?> cities(@RequestParam Long stateId) {
        APICustomize<List<ItsRctCityResponse>> response = cityService.cities(stateId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/city")
    public ResponseEntity<?> city(@RequestParam Long id) {
        APICustomize<ItsRctCityResponse> response = cityService.city(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/city")
    public ResponseEntity<?> create(@RequestBody AddCityRequest request) {
        APICustomize<ItsRctCityResponse> response = cityService.create(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/city")
    public ResponseEntity<?> update(@RequestParam Long id, @RequestBody AddCityRequest request) {
        APICustomize<ItsRctCityResponse> response = cityService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/city")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = cityService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
