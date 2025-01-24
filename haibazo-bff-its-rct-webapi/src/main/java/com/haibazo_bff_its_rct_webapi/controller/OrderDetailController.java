package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctOrderDetailResponse;
import com.haibazo_bff_its_rct_webapi.service.OrderDetailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @GetMapping("/user/order-detail/order-details")
    public ResponseEntity<?> orderDetails(@RequestParam Long orderId, HttpServletRequest httpRequest) {
        // Lấy header Authorization từ yêu cầu
        String authorizationHeader = httpRequest.getHeader("Authorization");
        APICustomize<List<ItsRctOrderDetailResponse>> response = orderDetailService.orderDetails(orderId, authorizationHeader);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
