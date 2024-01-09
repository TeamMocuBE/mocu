package com.example.mocu.Common.exception;

import com.example.mocu.Common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class StoreException extends RuntimeException{
    private final ResponseStatus responseStatus;

    public StoreException(ResponseStatus responseStatus){
        super(responseStatus.getMessage());
        this.responseStatus = responseStatus;
    }

    public StoreException(ResponseStatus responseStatus, String message){
        super(message);
        this.responseStatus = responseStatus;
    }
}
