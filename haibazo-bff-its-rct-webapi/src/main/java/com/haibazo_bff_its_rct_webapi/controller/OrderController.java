package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddOrderRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderResponse;
import com.haibazo_bff_its_rct_webapi.service.OrderService;
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
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/user/order")
    public ResponseEntity<?> add(@RequestBody AddOrderRequest request, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<ItsRctOrderResponse> response = orderService.create(request, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/order/orders")
    public ResponseEntity<?> orders(HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<List<ItsRctOrderResponse>> response = orderService.getOrdersByToken(authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
