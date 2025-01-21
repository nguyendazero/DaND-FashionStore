package com.haibazo_bff_its_rct_webapi.controller;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddCollectionProductRequest;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserNotificationRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctNotificationResponse;
import com.haibazo_bff_its_rct_webapi.model.NotificationUser;
import com.haibazo_bff_its_rct_webapi.service.NotificationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bff/its-rct/v1")
public class NotificationUserController {

    private final NotificationUserService notificationUserService;

    @PostMapping("/admin/notification-user")
    public ResponseEntity<?> create(@RequestBody AddUserNotificationRequest request) {
        notificationUserService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
