package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.Notification;
import com.haibazo_bff_its_rct_webapi.model.NotificationUser;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {

    @Query("SELECT nu.notification FROM NotificationUser nu WHERE nu.user.id = :userId")
    List<Notification> findNotificationsByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndNotificationId(Long userId, Long notificationId);

    List<NotificationUser> findByUserId(Long userId);

}
