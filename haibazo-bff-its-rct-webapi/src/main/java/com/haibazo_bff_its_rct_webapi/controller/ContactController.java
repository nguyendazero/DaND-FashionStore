package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddContactRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctContactResponse;
import com.haibazo_bff_its_rct_webapi.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/public/contact")
    public ResponseEntity<?> create(@RequestBody AddContactRequest request, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");

        // Gọi service để thêm contact, truyền header JWT
        APICustomize<ItsRctContactResponse> response = contactService.add(request, authorizationHeader);

        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("admin/contact")
    public ResponseEntity<?> contact(@RequestParam Long id) {
        APICustomize<ItsRctContactResponse> response = contactService.contact(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("admin/contact/contacts")
    public ResponseEntity<?> contacts() {
        APICustomize<List<ItsRctContactResponse>> response = contactService.contacts();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/contact")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = contactService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
