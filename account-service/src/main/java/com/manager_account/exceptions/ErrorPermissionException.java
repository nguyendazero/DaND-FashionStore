package com.manager_account.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ErrorPermissionException extends RuntimeException {

    public ErrorPermissionException() {
        super();
    }

}


