package com.example.mocu.Dto.Push;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostOwnerPushTokenRequest {
    private long ownerId;
    private String deviceId;
    private String deviceToken;
}
