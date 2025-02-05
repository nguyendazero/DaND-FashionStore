package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddNotificationRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctNotificationResponse;
import com.haibazo_bff_its_rct_webapi.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1/ecommerce")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/admin/notification/notifications")
    public ResponseEntity<?> notifications() {
        APICustomize<List<ItsRctNotificationResponse>> response = notificationService.notifications();
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @PostMapping("/admin/notification")
    public ResponseEntity<?> create(@RequestBody AddNotificationRequest request) {
        APICustomize<ItsRctNotificationResponse> response = notificationService.create(request);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/notification")
    public ResponseEntity<?> notification(@RequestParam Long id) {
        APICustomize<ItsRctNotificationResponse> response = notificationService.notification(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @GetMapping("/user/notification-by-user-id")
    public ResponseEntity<?> notificationByUserId(@RequestParam Long userId) {
        APICustomize<ItsRctNotificationResponse> response = notificationService.notificationByUserId(userId);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

    @DeleteMapping("/admin/notification")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        APICustomize<String> response = notificationService.delete(id);
        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
    }

}
