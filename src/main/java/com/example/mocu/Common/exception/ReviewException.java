package com.example.mocu.Common.exception;

import com.example.mocu.Common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public ReviewException(ResponseStatus exceptionStatus){
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public ReviewException(ResponseStatus exceptionStatus, String message){
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
