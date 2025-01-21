package com.haibazo_bff_its_rct_webapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CouponAlreadyUsedException extends RuntimeException {

    public CouponAlreadyUsedException() {
        super();
    }

}
