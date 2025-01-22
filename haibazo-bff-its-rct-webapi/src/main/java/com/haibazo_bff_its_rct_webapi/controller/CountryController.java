package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCountryRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCountryResponse;
import com.haibazo_bff_its_rct_webapi.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/public/country/countries")
    public ResponseEntity<?> countries(){
        APICustomize<List<ItsRctCountryResponse>> response = countryService.countries();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/country")
    public ResponseEntity<?> country(@RequestParam String code){
        APICustomize<ItsRctCountryResponse> response = countryService.countryByCode(code);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/country")
    public ResponseEntity<?> createCountry(@RequestBody AddCountryRequest request){
        APICustomize<ItsRctCountryResponse> response = countryService.addCountry(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/country")
    public ResponseEntity<?> deleteCountry(@RequestParam String code){
        APICustomize<String> response = countryService.deleteCountry(code);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/country")
    public ResponseEntity<?> updateCountry(@RequestParam String code, @RequestBody AddCountryRequest request){
        APICustomize<ItsRctCountryResponse> response = countryService.updateCountry(code, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
