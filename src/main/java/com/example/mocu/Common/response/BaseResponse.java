package com.example.mocu.Common.response;


import com.example.mocu.Common.response.status.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import static com.example.mocu.Common.response.status.BaseResponseStatus.*;

@Getter
@JsonPropertyOrder({"code", "status", "message", "result"})                 // 응답 form
public class BaseResponse<T> implements ResponseStatus {
    private final int code;
    private final int status;
    private final String messages;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public BaseResponse(T result){
        this.code = SUCCESS.getCode();
        this.status = SUCCESS.getStatus();
        this.messages = SUCCESS.getMessage();
        this.result = result;
    }

    @Override
    public int getCode(){
        return code;
    }

    @Override
    public int getStatus(){
        return status;
    }

    @Override
    public String getMessage(){
        return messages;
    }
}
