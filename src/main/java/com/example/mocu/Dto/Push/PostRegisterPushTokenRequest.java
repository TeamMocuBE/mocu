package com.example.mocu.Dto.Push;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRegisterPushTokenRequest {
    private long userId;
    private String deviceId;
    private String deviceToken;
}
