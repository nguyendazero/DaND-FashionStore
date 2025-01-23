package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddLanguageRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctLanguageResponse;
import com.haibazo_bff_its_rct_webapi.exception.GlobalExceptionHandler;
import com.haibazo_bff_its_rct_webapi.service.LanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class LanguageController {

    private final LanguageService languageService;
    private final GlobalExceptionHandler globalExceptionHandler;

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

    @GetMapping("/public/language/choose-language")
    public ResponseEntity<String> chooseLanguage(@RequestParam String code) {
        String fileName = switch (code) {
            case "vn" -> "messages_vn_VN.properties";
            case "us" -> "messages_en_US.properties";
            default -> null;
        };

        if (fileName == null) return ResponseEntity.badRequest().body("Invalid language code: " + code);

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) throw new IOException("File not found: " + fileName);
            globalExceptionHandler.loadMessages(input);
            return ResponseEntity.ok("Language set to: " + code);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
