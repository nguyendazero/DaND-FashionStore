package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCurrencyRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddDiscountRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctCurrencyResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctDiscountResponse;
import com.haibazo_bff_its_rct_webapi.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping("/admin/discount/discounts")
    public ResponseEntity<?> discounts(){
        APICustomize<List<ItsRctDiscountResponse>> response = discountService.discounts();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/admin/discount")
    public ResponseEntity<?> discount(@RequestParam Long id){
        APICustomize<ItsRctDiscountResponse> response = discountService.discount(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/discount")
    public ResponseEntity<?> add(@RequestBody AddDiscountRequest request){
        APICustomize<ItsRctDiscountResponse> response = discountService.add(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/discount")
    public ResponseEntity<?> delete(@RequestParam Long id){
        APICustomize<String> response = discountService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
