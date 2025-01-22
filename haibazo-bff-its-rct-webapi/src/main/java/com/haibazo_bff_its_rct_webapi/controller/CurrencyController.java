package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCurrencyRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCurrencyResponse;
import com.haibazo_bff_its_rct_webapi.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/public/currency/currencies")
    public ResponseEntity<?> currencies(){
        APICustomize<List<ItsRctCurrencyResponse>> response = currencyService.currencies();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/currency")
    public ResponseEntity<?> currency(@RequestParam String code){
        APICustomize<ItsRctCurrencyResponse> response = currencyService.getCurrencyByCode(code);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/currency")
    public ResponseEntity<?> addCurrency(@RequestBody AddCurrencyRequest request){
        APICustomize<ItsRctCurrencyResponse> response = currencyService.addCurrency(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/currency")
    public ResponseEntity<?> deleteCurrency(@RequestParam String code){
        APICustomize<String> response = currencyService.deleteCurrency(code);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/currency")
    public ResponseEntity<?> updateCurrency(@RequestParam String code, @RequestBody AddCurrencyRequest request){
        APICustomize<ItsRctCurrencyResponse> response = currencyService.updateCurrency(code, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }
}
