package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddUserNotificationRequest;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceAlreadyExistsException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.NotificationRepository;
import com.haibazo_bff_its_rct_webapi.repository.NotificationUserRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.NotificationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationUserServiceImpl implements NotificationUserService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationUserRepository notificationUserRepository;

    @Override
    public APICustomize<Void> add(AddUserNotificationRequest request) {

        // Lấy thông tin collection
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        // Lấy thông tin product
        Notification notification = notificationRepository.findById(request.getNotificationId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", request.getNotificationId().toString()));

        // Kiểm tra xem CollectionProduct đã tồn tại chưa
        if (notificationUserRepository.existsByUserIdAndNotificationId(user.getId(), notification.getId())) {
            throw new ResourceAlreadyExistsException("NotificationUser", "userId va NotificationId",
                    "User ID: " + user.getId() + ", Notification ID: " + notification.getId());
        }

        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setUser(user);
        notificationUser.setNotification(notification);

        notificationUserRepository.save(notificationUser);

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), null);
    }
}
