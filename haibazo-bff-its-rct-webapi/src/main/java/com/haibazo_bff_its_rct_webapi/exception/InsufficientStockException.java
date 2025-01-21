package com.haibazo_bff_its_rct_webapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InsufficientStockException extends RuntimeException {

    private final String productName;

    public InsufficientStockException(String productName) {
        super();
        this.productName = productName;
    }

}

