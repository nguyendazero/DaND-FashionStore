package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAddressRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAddressResponse;
import com.haibazo_bff_its_rct_webapi.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/public/address/addresses")
    public ResponseEntity<?> addresses(@RequestParam Long wardId) {
        APICustomize<List<ItsRctAddressResponse>> response = addressService.addresses(wardId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/public/address")
    public ResponseEntity<?> address(@RequestParam Long id) {
        APICustomize<ItsRctAddressResponse> response = addressService.address(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/address")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = addressService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PutMapping("/admin/address")
    public ResponseEntity<?> update(@RequestParam Long id, @RequestBody AddAddressRequest request) {
        APICustomize<ItsRctAddressResponse> response = addressService.update(id, request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/address")
    public ResponseEntity<?> create(@RequestBody AddAddressRequest request) {
        APICustomize<ItsRctAddressResponse> response = addressService.create(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
