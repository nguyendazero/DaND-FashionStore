package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddOrderRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderResponse;
import com.haibazo_bff_its_rct_webapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/user/order")
    public ResponseEntity<?> add(@RequestBody AddOrderRequest request) {
        APICustomize<ItsRctOrderResponse> response = orderService.create(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/order/orders")
    public ResponseEntity<?> orders(@RequestParam Long userId) {
        APICustomize<List<ItsRctOrderResponse>> response = orderService.getOrdersByUserId(userId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
