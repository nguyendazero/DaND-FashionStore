package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddLanguageRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctLanguageResponse;
import com.haibazo_bff_its_rct_webapi.service.LanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping("/public/language/languages")
    public ResponseEntity<?> languages() {
        APICustomize<List<ItsRctLanguageResponse>> response = languageService.languages();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/language")
    public ResponseEntity<?> addLanguage(@ModelAttribute @Valid AddLanguageRequest request) {
        APICustomize<ItsRctLanguageResponse> response = languageService.addLanguage(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/language")
    public ResponseEntity<?> language(@RequestParam String code){
        APICustomize<ItsRctLanguageResponse> response = languageService.getLanguageByCode(code);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/language")
    public ResponseEntity<?> deletedLanguage(@RequestParam String code){
        APICustomize<String> response = languageService.deleteByCode(code);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }


}
