package com.haibazo_bff_its_rct_webapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.CONFLICT)
public class CartEmptyException extends RuntimeException {

    private final Long userId;

    public CartEmptyException(Long userId) {
        super();
        this.userId = userId;
    }
}
