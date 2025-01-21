package com.haibazo_bff_its_rct_webapi.service;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserNotificationRequest;

public interface NotificationUserService {

    public APICustomize<Void> add(AddUserNotificationRequest request);

}
