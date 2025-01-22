package com.manager_account.listener;

import com.manager_account.dto.response.APICustomize;
import com.manager_account.enums.ApiError;
import com.manager_account.event.DeleteUserEvent;
import com.manager_account.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountEventListener {

    private final AccountRepository accountRepository;

    @KafkaListener(topics = "userDeletionTopic")
    @Transactional
    public APICustomize<String> handleDeleteUserEvent(DeleteUserEvent deleteUserEvent) {
        // Xóa tài khoản bằng haibazoAccountId
        accountRepository.deleteByHaibazoAccountId(deleteUserEvent.getHaibazoAccountId());
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted category with haibazoAccountId = " + deleteUserEvent.getHaibazoAccountId());
    }
}
