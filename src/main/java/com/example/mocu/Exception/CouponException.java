package com.example.mocu.Exception;

import com.example.mocu.Common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class CouponException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public CouponException(ResponseStatus exceptionStatus){
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public CouponException(ResponseStatus exceptionStatus, String message){
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
