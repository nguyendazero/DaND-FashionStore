package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddNotificationRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctNotificationResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Notification;
import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.repository.NotificationRepository;
import com.haibazo_bff_its_rct_webapi.repository.NotificationUserRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.NotificationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationUserRepository notificationUserRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public APICustomize<List<ItsRctNotificationResponse>> notifications() {
        List<Notification> notifications = notificationRepository.findAll();
        List<ItsRctNotificationResponse> notificationResponses = notifications.stream()
                .map(notification -> new ItsRctNotificationResponse(
                        notification.getId(),
                        notification.getContent(),
                        notification.getCreatedAt(),
                        notification.getUpdatedAt()))
                .toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), notificationResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctNotificationResponse>> getNotificationsByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        List<Notification> notificationContents = notificationUserRepository.findNotificationsByUserId(userId);
        if (notificationContents.isEmpty()) {
            throw new ResourceNotFoundException("Notification", "userId", userId.toString());
        }
        List<ItsRctNotificationResponse> notificationResponses = notificationContents.stream()
                .map(notificationResponse -> new ItsRctNotificationResponse(
                        notificationResponse.getId(),
                        notificationResponse.getContent(),
                        notificationResponse.getCreatedAt(),
                        notificationResponse.getUpdatedAt()
                )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), notificationResponses);
    }

    @Override
    public APICustomize<ItsRctNotificationResponse> create(AddNotificationRequest request) {
        Notification notification = new Notification();
        notification.setContent(request.getContent());
        Notification saved = notificationRepository.save(notification);
        ItsRctNotificationResponse response = new ItsRctNotificationResponse(
                saved.getId(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    public APICustomize<ItsRctNotificationResponse> notification(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id.toString()));

        ItsRctNotificationResponse response = new ItsRctNotificationResponse(
                notification.getId(),
                notification.getContent(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    public APICustomize<String> delete(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id.toString()));

        notificationRepository.delete(notification);
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), "Successfully deleted notification with id = " + notification.getId());
    }

}
