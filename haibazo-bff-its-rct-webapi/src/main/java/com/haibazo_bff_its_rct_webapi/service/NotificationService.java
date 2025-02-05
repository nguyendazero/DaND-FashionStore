package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddNotificationRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctNotificationResponse;

import java.util.List;

public interface NotificationService {

    public APICustomize<List<ItsRctNotificationResponse>> notifications();

    public APICustomize<ItsRctNotificationResponse> create(AddNotificationRequest request);

    public APICustomize<ItsRctNotificationResponse> notification(Long id);

    public APICustomize<ItsRctNotificationResponse> notificationByUserId(Long userId);

    public APICustomize<String> delete(Long id);

}
